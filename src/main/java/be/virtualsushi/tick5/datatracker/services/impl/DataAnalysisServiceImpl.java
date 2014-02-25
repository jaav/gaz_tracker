package be.virtualsushi.tick5.datatracker.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.virtualsushi.tick5.datatracker.repositories.*;
import be.virtualsushi.tick5.datatracker.services.GoogleTranslateService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import twitter4j.Twitter;
import be.virtualsushi.tick5.datatracker.model.PopularSource;
import be.virtualsushi.tick5.datatracker.model.Subsidized;
import be.virtualsushi.tick5.datatracker.model.SubsidizedUrl;
import be.virtualsushi.tick5.datatracker.model.TweepTypes;
import be.virtualsushi.tick5.datatracker.model.Tweet;
import be.virtualsushi.tick5.datatracker.model.TweetObject;
import be.virtualsushi.tick5.datatracker.model.TweetObjectTypes;
import be.virtualsushi.tick5.datatracker.model.TweetStates;
import be.virtualsushi.tick5.datatracker.model.TwitterUser;
import be.virtualsushi.tick5.datatracker.services.DataAnalysisService;
import be.virtualsushi.tick5.datatracker.services.ImageProcessService;

@Service("dataAnalysisService")
public class DataAnalysisServiceImpl implements DataAnalysisService {

	private static final float BESTSELLER_LIMIT_FACTOR = 0.2f;

	private static final float RECENCY_REDUCE_FACTOR = 2f;

	private static final float OTHERLANG_REDUCE_FACTOR = 1f;

	private static final float POPULARSOURCE_REDUCE_FACTOR = 2f;

	private static final int SUBSIDIZEDURL_PROMOTION_FACTOR = 3;

	private static final int SUBSIDIZED_PROMOTION_FACTOR = 2;

	private static final float OTHERCOUNTRY_REDUCE_FACTOR = 4f;

	private static final Logger log = LoggerFactory.getLogger(DataAnalysisServiceImpl.class);

	@Autowired
	private TweetRepository tweetRepository;

	@Autowired
	private TwitterUserRepository twitterUserRepository;

	@Autowired
	private ImageProcessService imageProcessService;

	@Autowired
	private AwsRepository awsRepository;

	@Autowired
	private Twitter twitter;

	@Autowired
	private PopularSourceRepository popularSourceRepository;

	@Autowired
	private SubsidizedRepository subsidizedRepository;

	@Autowired
	private SubsidizedUrlRepository subsidizedUrlRepository;

	@Autowired
	private GoogleTranslateService googleTranslateService;

	@Value("${tracking.country}")
	private String trackingCountry;

	@Value("${tracking.language.1}")
	private String trackingLanguage1;

	@Value("${tracking.language.2}")
	private String trackingLanguage2;

	@Value("${tracking.language.3}")
	private String trackingLanguage3;

	@Value("${twitter.publish}")
	private Boolean twitterPublish;

	@Override
	public void analyseTweets(String aws_key) {
		try {
			List<Tweet> tweets = tweetRepository.getNotRatedTweets();
			log.debug("Found {} not-rated tweets", tweets.size());
			// Rating tweets.
			rateTweets(tweets);
			// Processing top rated tweets.
			processTopRatedTweets(tweetRepository.getTopRatedTweets(new PageRequest(0, 50, Direction.DESC, "rate")).getContent(), aws_key);
		} catch (Exception e) {
			log.error("Error analysing tweets.", e);
		}
	}

	private void processTopRatedTweets(List<Tweet> topRatedTweets, String aws_key) throws InterruptedException, ExecutionException, JsonGenerationException, JsonMappingException, IOException {
		log.debug("Top rating a list of {} tweets", topRatedTweets.size());
		List<Tweet> popularTweets = createPopularTweetsList(topRatedTweets);
		Map<Long, Future<File>> futureImageFiles = new HashMap<Long, Future<File>>();
		Map<Long, Tweet> tweetsToPost = new HashMap<Long, Tweet>();
		long id = System.currentTimeMillis();
		for (Tweet tweet : popularTweets) {
			Tweet topper = new Tweet();
			// topper.setLocation(tweet.getLocation());
			topper.setQuantity(tweet.getQuantity());
			topper.setRate(tweet.getRate());
			topper.setText(tweet.getText());
			topper.setUser(tweet.getUser());
			topper.setTweep(tweet.getTweep());
			topper.setId(id++);
			topper.setState(TweetStates.TOP_RATED);
			topper.setObjects(tweet.getObjects());
			if (StringUtils.isBlank(tweet.getImage())) {
				futureImageFiles.put(tweet.getId(), imageProcessService.createTweetImage(topper));
			}
			tweetsToPost.put(tweet.getId(), topper);
			try {
				if(twitterPublish && tweet.getRecencyFactor()>0.49 && tweet.getQuantity()>6 && tweet.getRate()>20)
					twitter.retweetStatus(tweet.getId());
					//twitter.updateStatus(tweet.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Long aLong : futureImageFiles.keySet()) {
			while (!futureImageFiles.get(aLong).isDone()) {
				log.debug("Image for " + aLong + " is not ready yet.");
				Thread.sleep(100);
			}
			File img = futureImageFiles.get(aLong).get();
			if (img != null)
				log.debug(futureImageFiles.get(aLong).get().getAbsolutePath() + futureImageFiles.get(aLong).get().getName());
		}
		for (Long aLong : futureImageFiles.keySet()) {
			if (futureImageFiles.containsKey(aLong)) {
				File imageFile = futureImageFiles.get(aLong).get();
				if (imageFile != null)
					tweetsToPost.get(aLong).setImage(imageFile.getName().substring(0, imageFile.getName().lastIndexOf(".")));
			}
			try {
				tweetRepository.save(tweetsToPost.get(aLong));
			} catch (Exception e) {
				log.warn("Tweet " + tweetsToPost.get(aLong).getId() + " could NOT be saved - " + e.getMessage());
			}

			// Set this tweep as opinion leader
			if (tweetsToPost.get(aLong).getQuantity() > 100) {
				TwitterUser dbUser = tweetsToPost.get(aLong).getUser();
				dbUser.setType(TweepTypes.LEADER);
				twitterUserRepository.save(dbUser);
			}
		}
		awsRepository.insertTweets(tweetsToPost, aws_key);
		/*
		 * for (Tweet tweet : tweetsToPost) {
		 * imageProcessService.createFilteredImages(tweet.getImage(),
		 * filterset); }
		 */
		// imageProcessService.createFilteredImages();
	}

	private List<Tweet> createPopularTweetsList(List<Tweet> topRatedTweets) {
		List<Tweet> popularTweets = new ArrayList<Tweet>();
		int counter = 0;
		while (popularTweets.size()<5 && topRatedTweets.size()>counter){
			Tweet tweet = topRatedTweets.get(counter++);
			log.debug("What's a Tweeptype? {}", TweepTypes.MEMBER.name());
			if(TweepTypes.MEMBER.name().equals(tweet.getUser().getType()) && (trackingLanguage1.equals(tweet.getUser().getLanguage()) || trackingLanguage2.equals(tweet.getUser().getLanguage()) || trackingLanguage2.equals(tweet.getUser().getLanguage())) && trackingCountry.equals(tweet.getUser().getLocation()))
				popularTweets.add(tweet);
			else if(getTrackingLanguage(tweet.getText())!=null && isTrackingCountry(tweet.getText()))
				popularTweets.add(tweet);
	    log.debug("We have {} tweets in the TOP list", popularTweets.size());
		}
		return popularTweets;
	}

	private void rateTweets(List<Tweet> tweets) {
		for (Tweet tweet : tweets) {
			try {
				int rate = tweet.getRawRate();
				/*
				 * if(!trackingCountry.equals(tweet.getLocation())) rate =
				 * (int)(rate/OTHERCOUNTRY_REDUCE_FACTOR);
				 */
				for (TweetObject object : tweet.getObjects()) {
					rate += object.getQuantity() * object.getQuantityFactor();
					if (object.getType() == TweetObjectTypes.URL && isSubsidizedUrl(object.getValue())) {
						rate += rate * SUBSIDIZEDURL_PROMOTION_FACTOR;
					}
				}
				if (isPopularSource(tweet.getUser().getScreenName()))
					rate = (int) (rate / POPULARSOURCE_REDUCE_FACTOR);
				if (isSubsidizedTweet(tweet.getText()))
					rate = rate * SUBSIDIZED_PROMOTION_FACTOR;
				Tweet fromDb = tweetRepository.findOne(tweet.getId());
				fromDb.setRate(Math.round(rate * tweet.getRecencyFactor()));
				fromDb.setRecencyFactor(tweet.getRecencyFactor() / RECENCY_REDUCE_FACTOR);
				fromDb.setState(TweetStates.RATED);
				tweetRepository.save(fromDb);
			} catch (Exception e) {
				log.warn("Not rating tweet " + tweet.getId());
			}
		}
	}

	private boolean isPopularSource(String screeName) {
		List<PopularSource> sources = popularSourceRepository.findAll();
		for (PopularSource source : sources) {
			if (screeName.equals(source.getScreenName()))
				return true;
		}
		return false;
	}

	private boolean isSubsidizedTweet(String text) {
		List<Subsidized> subsidizedList = subsidizedRepository.findAll();
		for (Subsidized subsidized : subsidizedList) {
			if (text.toLowerCase().contains(subsidized.getWord()))
				return true;
		}
		return false;
	}

	private boolean isSubsidizedUrl(String url) {
		List<SubsidizedUrl> subsidizedUrlList = subsidizedUrlRepository.findAll();
		for (SubsidizedUrl subsidizedUrl : subsidizedUrlList) {
			if (url.toLowerCase().contains(subsidizedUrl.getWord()))
				return true;
		}
		return false;
	}


	private String getTrackingLanguage(String content) {
		return googleTranslateService.getTrackingLanguage(content);
	}


	private boolean isTrackingCountry(String content) {
			return googleTranslateService.isTrackingCountry(content);
	}

}
