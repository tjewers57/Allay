package org.allaymc.api.scoreboard;

import org.allaymc.api.scoreboard.scorer.FakeScorer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ScoreboardPluginHelper {

    /**
     * Convenient interface for plugins to display scores
     *
     * @param scoreboard Scoreboard to be modified
     * @param text  Name of FakeScorer
     * @param score Score
     *
     * @return Whether the addition was successful
     */
    public static boolean addLine(Scoreboard scoreboard, String text, int score) {
        var fakeScorer = new FakeScorer(text);
        return scoreboard.addLine(new ScoreboardLine(scoreboard, fakeScorer, score));
    }

    /**
     * Convenient interface for plugins
     * <p>
     * Set the content of the scoreboard in the order of the List (using FakeScorer as the tracker)
     * <p>
     * It will overwrite all previous lines
     *
     * @param lines String content to set
     */
    public static void setLines(Scoreboard scoreboard, List<String> lines) {
        scoreboard.removeAllLines(false);
        AtomicInteger score = new AtomicInteger();
        lines.forEach(str -> {
            var scorer = new FakeScorer(str);
            scoreboard.getLines().put(scorer, new ScoreboardLine(scoreboard, scorer, score.getAndIncrement()));
        });
        scoreboard.resend();
    }
}
