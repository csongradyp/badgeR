package com.noe.badger.event;


import com.noe.badger.AchievementController;
import com.noe.badger.event.handler.AchievementUnlockedHandlerWrapper;
import com.noe.badger.event.handler.IAchievementUnlockedHandler;
import com.noe.badger.event.handler.IScoreUpdateHandler;
import com.noe.badger.event.handler.ScoreUpdateHandlerWrapper;
import com.noe.badger.event.message.Achievement;
import com.noe.badger.event.message.Score;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import net.engio.mbassy.bus.MBassador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventBus {

    private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);
    private static final EventBus INSTANCE = new EventBus();

    private AchievementController controller;

    private final MBassador<Score> scoreUpdateBus;
    private final MBassador<Achievement> unlockedBus;
    private final Collection<AchievementUnlockedHandlerWrapper> unlockedSubscribers;
    private final Collection<ScoreUpdateHandlerWrapper> scoreUpdateSubscribers;

    private EventBus() {
        scoreUpdateBus = new MBassador<>();
        unlockedBus = new MBassador<>();

        unlockedSubscribers = new ArrayList<>();
        scoreUpdateSubscribers = new ArrayList<>();

        registerShutdownHook(scoreUpdateBus, unlockedBus);
    }

    private void registerShutdownHook(final MBassador<?>... mBassadors) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (MBassador<?> eventBus : mBassadors) {
                eventBus.shutdown();
            }
        }));
    }

    public static void subscribeOnUnlock(final AchievementUnlockedHandlerWrapper handler) {
        INSTANCE.unlockedSubscribers.add(handler);
        INSTANCE.unlockedBus.subscribe(handler);
    }

    public static void unSubscribeOnUnlock(final IAchievementUnlockedHandler handler) {
        final Optional<AchievementUnlockedHandlerWrapper> registeredHandler = INSTANCE.unlockedSubscribers.stream().filter(
                achievementUnlockedHandlerWrapper -> achievementUnlockedHandlerWrapper.getWrapped().equals(handler)).findFirst();
        if (registeredHandler.isPresent()) {
            INSTANCE.unlockedBus.unsubscribe(registeredHandler.get());
        }
    }

    public static void publishUnlocked(final Achievement achievement) {
        INSTANCE.unlockedBus.publish(achievement);
        LOG.info("Achievement unlocked event published. Achievement - title: {}, level: {}", achievement.getTitle(), achievement.getLevel());
    }

    public static void subscribeOnScoreChanged(final ScoreUpdateHandlerWrapper handler) {
        INSTANCE.scoreUpdateSubscribers.add(handler);
        INSTANCE.scoreUpdateBus.subscribe(handler);
    }

    public static void unSubscribeOnScoreChanged(final IScoreUpdateHandler handler) {
        final Optional<ScoreUpdateHandlerWrapper> registeredHandler = INSTANCE.scoreUpdateSubscribers.stream().filter(wrapper -> wrapper.getWrapped().equals(handler)).findFirst();
        if (registeredHandler.isPresent()) {
            INSTANCE.scoreUpdateBus.unsubscribe(registeredHandler.get());
        }
    }

    public static void publishScoreChanged(final Score score) {
        INSTANCE.scoreUpdateBus.publish(score);
        LOG.info("Achievement score {} updated with value {}", score.getEvent(), score.getValue());
    }

    public static void triggerEvent(final String id, final Long score) {
        INSTANCE.controller.triggerEvent(id, score);
    }

    public static void triggerEvent(final String id, final Collection<String> owners) {
        INSTANCE.controller.triggerEvent(id, owners);
    }

    public static void setController(final AchievementController controller) {
        INSTANCE.controller = controller;
    }

    public static void unlock(final String achievementId, final String triggerValue) {
        INSTANCE.controller.unlock(achievementId, triggerValue);
    }

    public static void unlock(final String achievementId, final String triggerValue, final Collection<String> owners) {
        INSTANCE.controller.unlock(achievementId, triggerValue, owners);
    }

    public static void checkAll() {
        INSTANCE.controller.checkAll();
    }
}
