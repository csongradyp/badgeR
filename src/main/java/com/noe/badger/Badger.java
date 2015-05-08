package com.noe.badger;

import com.noe.badger.bundle.domain.IAchievement;
import com.noe.badger.bundle.domain.achievement.AchievementType;
import com.noe.badger.bundle.parser.AchievementDefinitionFileParser;
import com.noe.badger.event.EventBus;
import com.noe.badger.event.handler.AchievementUnlockedHandlerWrapper;
import com.noe.badger.event.handler.IAchievementUnlockedHandler;
import com.noe.badger.event.handler.IScoreUpdateHandler;
import com.noe.badger.event.handler.ScoreUpdateHandlerWrapper;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Badger {

    private static final String CONTEXT_XML_PATH = "META-INF/beans.xml";

    private final AchievementDefinitionFileParser parser;
    private final AchievementController controller;

    /**
     * Default constructor to set up Spring environment.
     */
    private Badger() {
        final ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT_XML_PATH);
        applicationContext.registerShutdownHook();
        parser = applicationContext.getBean(AchievementDefinitionFileParser.class);
        controller = applicationContext.getBean(AchievementController.class);
    }

    /**
     * Starts the BadgeR achievement engine without i18n support.
     *
     * @param inputStream {@link InputStream} instance which opened for the achievement definition file.
     * @param baseName    i18n properties file base name for internationalization support.<br/>
     *                    See more at <a href="http://csongradyp.github.io/badgeR/">BadgeR API documentation</a>.
     */
    public Badger(final InputStream inputStream, final String baseName) {
        this();
        controller.setDefinition(parser.parse(inputStream));
        controller.setInternationalizationBaseName(baseName);
    }

    /**
     * Starts the BadgeR achievement engine without i18n support.
     *
     * @param definitionFilePath Absolute path of the achievement definition file location.
     * @param baseName           i18n properties file base name for internationalization support.<br/>
     *                           See more at <a href="http://csongradyp.github.io/badgeR/">BadgeR API documentation</a>.
     */
    public Badger(final String definitionFilePath, final String baseName) {
        this(new File(definitionFilePath), baseName);
    }

    /**
     * Starts the BadgeR achievement engine without i18n support.
     *
     * @param definitionFile {@link File} instance which represents the achievement definition file.
     * @param baseName       i18n properties file base name for internationalization support.<br/>
     *                       See more at <a href="http://csongradyp.github.io/badgeR/">BadgeR API documentation</a>.
     */
    public Badger(final File definitionFile, final String baseName) {
        this();
        controller.setDefinition(parser.parse(definitionFile));
        controller.setInternationalizationBaseName(baseName);
    }

    /**
     * Starts the BadgeR achievement engine without i18n support.
     *
     * @param inputStream {@link InputStream} instance which opened for the achievement definition file.
     */
    public Badger(final InputStream inputStream) {
        this();
        controller.setDefinition(parser.parse(inputStream));
    }

    /**
     * Starts the BadgeR achievement engine without i18n support.
     *
     * @param definitionFilePath Absolute path of the achievement definition file location.
     */
    public Badger(final String definitionFilePath) {
        this(new File(definitionFilePath));
    }

    /**
     * Starts the BadgeR achievement engine without i18n support.
     *
     * @param definitionFile {@link File} instance which represents the achievement definition file.
     */
    public Badger(final File definitionFile) {
        this();
        controller.setDefinition(parser.parse(definitionFile));
    }

    /**
     * Set the language to use for resolving unlocked achievements title and description.
     *
     * @param locale {@link java.util.Locale} instance. Default locale is {@code Locale.ENGLISH} (en)
     */
    public void setLocale(final Locale locale) {
        controller.setLocale(locale);
    }

    /**
     * Unlock achievement directly without triggering any events.
     *
     * @param type           Type of the achievement: Single, Counter, Time, Time range, Date or Composite. See more at {@link AchievementType}
     * @param id             Defined unique id of the achievement.
     * @param triggeredValue Optional attribute to provide more information of the unlock.
     */
    public void unlock(final AchievementType type, final String id, final String triggeredValue) {
        controller.unlock(type, id, triggeredValue);
    }

    /**
     * Unlock achievement directly without triggering any events.
     *
     * @param type           Type of the achievement: Single, Counter, Time, Time range, Date or Composite. See more at {@link AchievementType}
     * @param id             Defined unique id of the achievement.
     * @param triggeredValue Optional attribute to provide more information of the unlock.
     * @param owners         Owners of the unlocked achievement. Basically the source of the event, the player, the newly created thing ...
     */
    public void unlock(final AchievementType type, final String id, final String triggeredValue, final String... owners) {
        controller.unlock(type, id, triggeredValue, owners);
    }

    /**
     * Returns all defined achievements without any sorting.
     *
     * @return {@link Collection} of {@link IAchievement} instances.
     */
    public Collection<IAchievement> getAllAchivement() {
        return controller.getAll();
    }

    /**
     * Returns all defined achievements belong to the given owner.
     *
     * @param owner Owners of the unlocked achievement.
     * @return {@link Collection} of {@link IAchievement} instances.
     */
    public Collection<IAchievement> getAchievementsByOwner(final String owner) {
        return controller.getByOwner(owner);
    }

    /**
     * Returns all defined achievements sorted by event subsciprions.
     *
     * @return {@link Map} of event name and {@link IAchievement} pairs.
     */
    public Map<String, Set<IAchievement>> getAllAchievementByEvent() {
        return controller.getAllByEvents();
    }

    /**
     * Triggers the given event and increment its counter by one.
     *
     * @param event Previously defined event in the achievement definition file.
     */
    public void triggerEvent(final String event) {
        controller.triggerEvent(event);
    }

    /**
     * Triggers the given event and sets its counter by the given score.
     *
     * @param event Previously defined event in the achievement definition file.
     * @param score new value of the event counter.
     */
    public void triggerEvent(final String event, final Long score) {
        controller.triggerEvent(event, score);
    }

    /**
     * Triggers the given event and sets its counter by the given score.
     *
     * @param event Previously defined event in the achievement definition file.
     * @param highScore new value of the event counter. New value will be only applied if its greater than the stored one.
     */
    public void triggerEventWithHighScore(final String event, final Long highScore) {
        controller.triggerEventWithHighScore(event, highScore);
    }

    /**
     * Triggers the given event and increment its counter by one.
     *
     * @param event  Previously defined event in the achievement definition file.
     * @param owners Owners of the unlocked achievement. Basically the source of the event, the player, the newly created thing ...
     */
    public void triggerEvent(final String event, final Collection<String> owners) {
        controller.triggerEvent(event, owners);
    }

    /**
     * Triggers the given event and increment its counter by one.
     *
     * @param event  Previously defined event in the achievement definition file.
     * @param owners Owners of the unlocked achievement. Basically the source of the event, the player, the newly created thing ...
     */
    public void triggerEvent(final String event, final String... owners) {
        controller.triggerEvent(event, owners);
    }

    /**
     * Returns the current value of the event counter.
     *
     * @param event Previously defined event in the achievement definition file.
     * @return current value of event counter.
     */
    public Long getCurrentScore(final String event) {
        return controller.getCurrentScore(event);
    }

    /**
     * Subscribe a handler to receive achievement unlocked events.
     *
     * @param achievementUnlockedHandler {@link IAchievementUnlockedHandler} implementation to be register.
     */
    public void subscribeOnUnlock(final IAchievementUnlockedHandler achievementUnlockedHandler) {
        EventBus.subscribeOnUnlock(new AchievementUnlockedHandlerWrapper(achievementUnlockedHandler));
    }

    /**
     * Unsubscribe registered unlocked event handler.
     *
     * @param achievementUnlockedHandler previously registered {@link IAchievementUnlockedHandler} implementation.
     */
    public void unSubscribeOnUnlock(final IAchievementUnlockedHandler achievementUnlockedHandler) {
        EventBus.unSubscribeOnUnlock(achievementUnlockedHandler);
    }

    /**
     * Subscribe a handler to receive achievement event counter or score update events.
     *
     * @param achievementUpdateHandler {@link IScoreUpdateHandler} implementation to be register.
     */
    public void subscribeOnScoreChanged(final IScoreUpdateHandler achievementUpdateHandler) {
        EventBus.subscribeOnScoreChanged(new ScoreUpdateHandlerWrapper(achievementUpdateHandler));
    }

    /**
     * Unsubscribe registered score update event handler.
     *
     * @param achievementUpdateHandler previously registered {@link IScoreUpdateHandler} implementation.
     */
    public void unSubscribeOnScoreChanged(final IScoreUpdateHandler achievementUpdateHandler) {
        EventBus.unSubscribeOnScoreChanged(achievementUpdateHandler);
    }

    /**
     * Clears all unlocked achievements and counter/event states.
     * Data deletion after calling this method cannot be undone.
     */
    public void reset() {
        controller.reset();
    }

}
