/*
 * Trivia by MarCarrot, 2020
 */

/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.trivia.effects;

import me.marcarrots.trivia.Trivia;
import me.marcarrots.trivia.language.Lang;
import me.marcarrots.trivia.language.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameBossBar {

    private final boolean bossBarEnabled;
    private final Trivia trivia;
    private BossBar bossBar;

    public GameBossBar(Trivia trivia, boolean bossBarEnabled) {
        this.trivia = trivia;
        this.bossBarEnabled = bossBarEnabled;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void startBossBar(int totalQuestions) {
        if (!bossBarEnabled) {
            return;
        }
        bossBar = Bukkit.createBossBar(Lang.BOSS_BAR_START.format_single(), BarColor.YELLOW, BarStyle.SOLID);
        bossBar.setProgress(0);
        if (totalQuestions % 10 == 0) {
            if (totalQuestions % 20 == 0) {
                bossBar.setStyle(BarStyle.SEGMENTED_20);
            } else {
                bossBar.setStyle(BarStyle.SEGMENTED_10);
            }
        } else if (totalQuestions % 6 == 0) {
            if (totalQuestions % 12 == 0) {
                bossBar.setStyle(BarStyle.SEGMENTED_12);
            } else {
                bossBar.setStyle(BarStyle.SEGMENTED_6);
            }
        }
        bossBar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach((p) -> bossBar.addPlayer(p));
    }

    public void perRoundBossBarUpdate(int currentQuestion, int totalQuestions) {
        if (!bossBarEnabled) {
            return;
        }
        bossBar.setTitle(Lang.BOSS_BAR_INFO.format_single(new Placeholder.PlaceholderBuilder()
                .questionNum(currentQuestion)
                .totalQuestionNum(totalQuestions)
                .build()
        ));
        bossBar.setColor(BarColor.RED);
        bossBar.setProgress(((float) currentQuestion - 1) / totalQuestions);
    }

    public void fillAfterAnswer(BarColor color, int currentQuestion, int totalQuestions) {
        if (!bossBarEnabled) {
            return;
        }
        bossBar.setColor(color);
        double incrementAmt = 1 / ((double) totalQuestions * 20);
        double goal = ((float) currentQuestion - 1) / totalQuestions;
        incrementBossBar(incrementAmt, goal);
    }

    public void fillAfterStop() {
        if (!bossBarEnabled) {
            return;
        }
        double incrementAmt = 0.05;
        double goal = 1;
        incrementBossBar(incrementAmt, goal);
    }

    private void incrementBossBar(double incrementAmt, double goal) {
        new BukkitRunnable() {
            @Override
            public void run() {
                double currentProgress = bossBar.getProgress();
                double amtToSet = currentProgress + incrementAmt;
                if (currentProgress < goal) {
                    if (amtToSet >= 1) {
                        bossBar.setProgress(1);
                        this.cancel();
                    } else if (amtToSet >= goal) {
                        bossBar.setProgress(goal);
                    } else {
                        currentProgress = amtToSet;
                        bossBar.setProgress(currentProgress);
                    }
                } else {
                    this.cancel();
                }
            }

        }.runTaskTimer(trivia, 0, 1);
    }

    public void hideBossBar() {
        if (!bossBarEnabled) {
            return;
        }
        bossBar.setVisible(true);
        bossBar.removeAll();
    }

    public void gameOverBossBar(BarColor barColor, String initialTitle) {
        if (!bossBarEnabled) {
            return;
        }
        bossBar.setTitle(initialTitle);
        bossBar.setColor(barColor);
        new BukkitRunnable() {
            boolean turn = false;

            @Override
            public void run() {
                if (turn) {
                    hideBossBar();
                    this.cancel();
                }
                bossBar.setTitle(Lang.BOSS_BAR_THANKS.format_single());
                turn = true;
            }
        }.runTaskTimer(trivia, 100, 100);
    }

    public void showBarToPlayer(Player player) {
        bossBar.addPlayer(player);
    }

}
