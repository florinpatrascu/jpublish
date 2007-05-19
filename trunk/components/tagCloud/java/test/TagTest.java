/*
 * Copyright (c) 2007, Florin T.PATRASCU.
 * All Rights Reserved.
 */


import ca.flop.jpublish.tags.Article;
import ca.flop.jpublish.tags.Tag;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Saturday; May 19, 2007)
 */
public class TagTest extends TestCase {
    Tag tag;

    public void testTag() throws Exception {
        String[] imageTagsDefinition = tags;
        Article article = new Article("700-12345678");
        for (int i = 0; i < imageTagsDefinition.length; i++) {
            String sTag = imageTagsDefinition[i];
            String[] props = StringUtils.split(sTag, ",");

            Tag tag = article.addTag(props[0], Long.parseLong(props[1]));
            tag.setProperty("score", props[2]);

        }
        Assert.assertTrue(article.getNumberOfTags() == tags.length);
        List tagList = new ArrayList(Arrays.asList(imageTagsDefinition));
        // todo: create more tests
        // article.getProperties().put("tags", tagList);
    }


    private String[] tags = {"people,73,38.81624211", "woman,29,12.47664925", "women,23,10.39720771",
            "jump,19,7.624618986", "fun,24,4.852030264", "fashion,18,4.852030264", "hip,11,4.158883083",
            "white,6,3.465735903", "New Images,39,2.079441542", "young,8,2.079441542", "jumping,6,1.386294361",
            "woman jumping,2,1.386294361", "people having fun,2,1.386294361",
            "fashion AND women's fashion,2,1.386294361", "shoes,6,0.693147181", "shoe,3,0.693147181",
            "young adult,2,0.693147181", "woman portrait,2,0.693147181", "young people,2,0.693147181",
            "trendy,2,0.693147181", "open arms,2,0.693147181", "angry young woman,2,0.693147181",
            "18-19 year old,1,0.693147181", "adult,1,0.693147181", "angry woman,1,0.693147181",
            "attitude,1,0.693147181", "backgrounds,1,0.693147181", "boot,1,0.693147181",
            "funky  teenager,1,0.693147181", "high,1,0.693147181", "HIGH FASHION,1,0.693147181",
            "people jump,1,0.693147181", "pose,1,0.693147181", "teen fashion,1,0.693147181",
            "woman fun,1,0.693147181", "woman in boots,1,0.693147181", "woman LEGS,1,0.693147181",
            "Woman Shoes,1,0.693147181", "woman with high boots,1,0.693147181", "woman young,1,0.693147181",
            "women in white,1,0.693147181", "style,6,0", "young adult jumping,6,0", "young hip,5,0",
            "people AND portrait,5,0", "up in the air,5,0", "young woman,3,0", "teen,2,0", "teens,2,0",
            "rebel,2,0", "make up,2,0", "cool,2,0", "fashionable woman,2,0", "emotions,2,0", "emotion,1,0",
            "eye contact,1,0", "costume   woman,1,0", "fashionable indoor,1,0", "20-30 year old,1,0",
            "adult (female),1,0", "angry,1,0", "boots,1,0", "footwear,1,0", "frustrated,1,0", "funky,1,0",
            "funky and people,1,0", "funky fashion,1,0", "human,1,0", "in the air,1,0", "jumping woman,1,0",
            "portrait,1,0", "portraits,1,0", "teen jump,1,0", "TEEN JUMPING,1,0", "teenage girl,1,0",
            "teenage girl fashion,1,0", "teenage girl portrait brown hair,1,0", "teenager,1,0",
            "teenager and jumping,1,0", "teenagers having fun,1,0", "people AND women only,1,0",
            "teens with makeup,1,0", "trendy woman,1,0", "very mad,1,0", "woman and rebellious,1,0",
            "woman boots,1,0", "woman jump,1,0", "woman make-up,1,0",
            "woman not illustration not monochrome and one person,1,0", "women portrait,1,0",
            "young teens jumping,1,0", "couple and reading,-2,0"};
}