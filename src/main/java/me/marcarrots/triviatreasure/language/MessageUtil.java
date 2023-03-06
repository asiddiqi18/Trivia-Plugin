/*
 * Trivia by MarCarrot, 2020
 */

package me.marcarrots.triviatreasure.language;


import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MessageUtil {

    private static final Pattern patternSolidHex = Pattern.compile("\\[(#[a-fA-F\\d]{6})]");
    private static final Pattern patternGradientHex = Pattern.compile("\\[(#[a-fA-F\\d]{6})-(#[a-fA-F\\d]{6})](.+?)(?=\\[#[a-fA-F\\d]{6}|$)");

    /**
     * @param input An input string with color codes to be translated. Color codes include '&' for old-style formatting
     *              colors, and '[#FFFFFF]' for hexadecimal colors (replace FFFFFF with desired color).
     * @return string The output string will contain colors, ready to be sent via Player.sendMessage(), etc..
     * @author MarCarrot
     */
    public static String HexColorMessage(String input) {
        Matcher matcher = patternSolidHex.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, ChatColor.of(matcher.group(1)).toString());
        }
        matcher.appendTail(sb);
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    /**
     * @param input An input string with color codes to be translated. Syntax is as follows:
     *              '[#AABBCC-112233] message here' where the message starts with the hex color
     *              #AABBCC and ends with 112233. Also, insert solid colors with '[#ABCDEF] message'
     *              within the same input for non-gradient effect.
     * @return string The output string will contain colors, ready to be sent via Player.sendMessage(), etc..
     * @author MarCarrot
     */
    public static String HexGradient(String input) {
        Matcher matcher = patternGradientHex.matcher(input);
        StringBuilder sb = new StringBuilder();
        int startIndex;
        int endIndex = 0;
        while (matcher.find()) {
            String firstGrad = matcher.group(1);
            String secondGrad = matcher.group(2);
            startIndex = matcher.start(3);
            endIndex = matcher.end(3);
            String sub = input.substring(startIndex, endIndex);
            int intervals = sub.length();

            if (intervals == 0) {
                return input;
            }

            Gradient colors = new Gradient(firstGrad, secondGrad, intervals);

            for (int i = 0; i < intervals; i++) {
                sb.append(ChatColor.of(colors.toColor()).toString()).append(sub.charAt(i));
                colors.nextInterval();
            }
        }

        sb.append(input.substring(endIndex));

        return HexColorMessage(sb.toString());
    }

}
