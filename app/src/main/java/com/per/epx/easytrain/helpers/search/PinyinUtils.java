package com.per.epx.easytrain.helpers.search;

import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PinyinUtils {

    public static <T extends ISearchable> List<T> searchKeyword(final String input, List<T> searchableSource){
        List<T> resultList = new ArrayList<>();//Result list
        String inputLowerCase = input.toLowerCase(Locale.CHINESE);
        for (T searchable : searchableSource) {
            if(searchable.getKeyword() == null) continue;
            if (searchable.getKeyword().toLowerCase(Locale.CHINESE).contains(inputLowerCase)
                    || searchable.getSearchToken().getSimpleSpellLowerCase().contains(inputLowerCase)
                    || searchable.getSearchToken().getWholeSpellLowerCase().contains(inputLowerCase)) {
                if (!resultList.contains(searchable)) {
                    resultList.add(searchable);
                }
            }
        }
        return resultList;
    }

    /**
     * Convert searchKey to SearchToken
     * @param searchKey search key value
     * @return SearchToken
     */
    public static SearchToken convertToSearchToken(String searchKey) {
        if (searchKey != null && searchKey.length() > 0) {
            //Generate basic parameters
            StringBuilder wholeBuilder = new StringBuilder();
            StringBuilder simpleBuilder = new StringBuilder();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            //Get pinyin head chars and whole spell
            char[] chars = searchKey.toCharArray();
            for (char current : chars) {
                simpleBuilder.append(getPinyinHead(current, defaultFormat));
                appendPinyinWholeSpell(wholeBuilder, current, defaultFormat);
            }
            return new SearchToken(simpleBuilder.toString(), wholeBuilder.toString(), searchKey);
        }
        return null;
    }

    /**
     * Convert searchKey to SearchToken
     * @param searchKey search key value
     * @return SearchToken with Polyphone support.
     */
    public static SearchToken convertToPolyphoneSupportSearchToken(String searchKey) {
        if (searchKey != null && searchKey.length() > 0) {
            //Generate basic parameters
            StringBuilder wholeBuilder = new StringBuilder();
            StringBuilder simpleBuilder = new StringBuilder();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            //Get pinyin head chars and whole spell
            char[] chars = searchKey.toCharArray();
            for (char current : chars) {
                appendPinyinHeads(simpleBuilder, current, defaultFormat);
                appendPinyinWholeSpells(wholeBuilder, current, defaultFormat);
            }
            return new SearchToken(parseTheChineseByObject(discountTheChinese(simpleBuilder.toString())),
                    parseTheChineseByObject(discountTheChinese(wholeBuilder.toString())), searchKey);
        }
        return null;
    }

    public static String convertToPinYinHeadChar(String str) {
        StringBuilder convert = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        char[] chars = str.toCharArray();
        for (char word : chars) {
            convert.append(getPinyinHead(word, defaultFormat));
        }
        return convert.toString();
    }

    public static String converterToWholeSpell(String chinese) {
        StringBuilder wholeBuilder = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        char[] chars = chinese.toCharArray();
        for (char current : chars) {
            appendPinyinWholeSpell(wholeBuilder, current, defaultFormat);
        }
        return wholeBuilder.toString();
    }

    private static char getPinyinHead(char current, HanyuPinyinOutputFormat format){
        if (current <= 128) {
            return current;
        } else {
            String[] pinyinArray = new String[0];
            try {
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(current, format);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            if (pinyinArray != null && pinyinArray.length > 0) {
                return pinyinArray[0].charAt(0);
            } else {
                return current;
            }
        }
    }

    private static void appendPinyinHeads(StringBuilder builder, char current, HanyuPinyinOutputFormat format){
        if (current <= 128) {
            builder.append(current);
        } else {
            String[] pinyinArray = new String[0];
            try {
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(current, format);
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                badHanyuPinyinOutputFormatCombination.printStackTrace();
            }
            if (pinyinArray != null && pinyinArray.length > 0) {
                for (String pinyin : pinyinArray){
                    builder.append(pinyin.charAt(0)).append(',');
                }
                builder.deleteCharAt(builder.length() - 1);
            } else {
                builder.append(current);
            }
        }
        builder.append(" ");
    }

    private static void appendPinyinWholeSpell(StringBuilder builder, char current, HanyuPinyinOutputFormat format){
        if (current <= 128) {
            builder.append(current);
        } else {
            String[] whole = null;
            try {
                //Get whole spell for current Chinese char
                whole = PinyinHelper.toHanyuPinyinStringArray(
                        current, format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            if (whole != null && whole.length > 0) {
                builder.append(whole[0]);
            } else {
                builder.append(current);
            }
        }
    }

    private static void appendPinyinWholeSpells(StringBuilder builder, char current, HanyuPinyinOutputFormat format){
        if (current <= 128) {
            builder.append(current);
        } else {
            String[] whole = null;
            try {
                //Get whole spell for current Chinese char
                whole = PinyinHelper.toHanyuPinyinStringArray(
                        current, format);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            if (whole != null && whole.length > 0) {
                for (String pinyin : whole){
                    builder.append(pinyin).append(',');
                }
                builder.deleteCharAt(builder.length() - 1);
            } else {
                builder.append(current);
            }
        }
        builder.append(" ");
    }

    /**
     * Discount the repeat data from Polyphone
     *
     * @param input the input string
     * @return result
     */
    private static List<Map<String, Integer>> discountTheChinese(String input) {
        //The list after filter repeat pinyin
        List<Map<String, Integer>> mapList = new ArrayList<>();
        //For handing the Polyphone for each pinyin
        Map<String, Integer> onlyOne = null;
        String[] pinyinGroupArray = input.split(" ");
        //Get pinyin for each string
        for (String pinyinGroup : pinyinGroupArray) {
            onlyOne = new Hashtable<>();
            String[] pinyin = pinyinGroup.split(",");
            //Handing Polyphone
            for (String s : pinyin) {
                Integer count = onlyOne.get(s);
                if (count == null) {
                    onlyOne.put(s, 1);
                } else {
                    count++;
                    onlyOne.put(s, count);
                }
            }
            mapList.add(onlyOne);
        }
        return mapList;
    }

    /**
     * Analyzing and combine pinyin
     *
     * @return combine result
     */
    private static String parseTheChineseByObject(List<Map<String, Integer>> mapList) {
        Map<String, Integer> latest = null;
        //Looping each map
        for(Map<String, Integer> map : mapList){
            //Map for combination of current and previous
            Map<String, Integer> temp = new Hashtable<>();
            if(latest == null){
                for (String s : map.keySet()) {
                    temp.put(s, 1);
                }
            }else {
                //Combine string of this map with latest map
                for (String s : latest.keySet()) {
                    for (String s1 : map.keySet()) {
                        temp.put(s + s1, 1);
                    }
                }
                //Clean previous data
                if (temp.size() > 0) {
                    latest.clear();
                }
            }
            //Save temp of this time for next loop
            if (temp.size() > 0) {
                latest = temp;
            }
        }
        StringBuilder resultBuilder = new StringBuilder();
        if (latest != null) {
            //Get result
            for (String key : latest.keySet()) {
                resultBuilder.append(key).append(",");
            }
            if (resultBuilder.length() > 0) {
                resultBuilder.deleteCharAt(resultBuilder.length() - 1);
            }
        }
        return resultBuilder.toString();
    }


}
