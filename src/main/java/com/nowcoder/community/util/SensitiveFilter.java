package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String RELACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyWord;
            while ((keyWord = br.readLine()) != null) {
                this.addKeyWord(keyWord);
            }

        } catch (IOException e) {
            LOGGER.error("加载敏感词文件失败：" + e.getMessage());
        }

    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
//        指针1
        TrieNode tempNode = rootNode;
//        指针2
        int begin = 0;
//        指针3
        int position = 0;
//        结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
//            跳过符号
            if (isSymbol(c)) {
//                若指针1处于根节点，将此符号留着，让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

//            检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
//                也begin开头的词不是敏感词
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
//                发现敏感词，替换
                sb.append(RELACEMENT);
                begin = ++position;
                tempNode = rootNode;
            } else {
//                检查下一个字符
                if (position < text.length() - 1) {
                    position++;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 区别特殊符
     * 0XE85~0x9FFF 是东亚的文字范围
     *
     * @param c
     * @return
     */
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0X9FFF);
    }

    /**
     * 添加敏感词到前缀树中
     *
     * @param keyWord
     */
    private void addKeyWord(String keyWord) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyWord.length(); i++) {
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
//                初始子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
//            指向子节点
            tempNode = subNode;

            if (i == keyWord.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 前缀树
     */
    private class TrieNode {
        //        关键字结束的标识
        private boolean isKeyWordEnd = false;

        //        当前节点的字节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        //        关键字结束的方法
        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //        添加子节点的方法
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //        后期字节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

}
