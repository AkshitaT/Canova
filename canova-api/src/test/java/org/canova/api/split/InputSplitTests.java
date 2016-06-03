/*
 *
 *  *
 *  *  * Copyright 2016 Skymind,Inc.
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *
 */
package org.canova.api.split;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import org.canova.api.io.filters.BalancedPathFilter;
import org.canova.api.io.filters.RandomPathFilter;
import org.canova.api.io.labels.ParentPathLabelGenerator;
import org.canova.api.io.labels.PatternPathLabelGenerator;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author saudet
 */
public class InputSplitTests {

    @Test
    public void testSample() throws URISyntaxException {
        BaseInputSplit split = new BaseInputSplit() {
            {
                String[] paths = {
                    "label0/group1_img.tif",
                    "label1/group1_img.jpg",
                    "label2/group1_img.png",
                    "label3/group1_img.jpeg",
                    "label4/group1_img.bmp",
                    "label5/group1_img.JPEG",
                    "label0/group2_img.JPG",
                    "label1/group2_img.TIF",
                    "label2/group2_img.PNG",
                    "label3/group2_img.jpg",
                    "label4/group2_img.jpg",
                    "label5/group2_img.wtf" };

                locations = new URI[paths.length];
                for (int i = 0; i < paths.length; i++) {
                    locations[i] = new URI("file:///" + paths[i]);
                }
            }

            @Override
            public void write(DataOutput out) throws IOException {
            }

            @Override
            public void readFields(DataInput in) throws IOException {
            }
        };

        Random random = new Random(42);
        String[] extensions = {"tif", "jpg", "png", "jpeg", "bmp", "JPEG", "JPG", "TIF", "PNG"};
        ParentPathLabelGenerator parentPathLabelGenerator = new ParentPathLabelGenerator();
        PatternPathLabelGenerator patternPathLabelGenerator = new PatternPathLabelGenerator("_", 0);
        RandomPathFilter randomPathFilter = new RandomPathFilter(random, extensions);
        RandomPathFilter randomPathFilter2 = new RandomPathFilter(random, extensions, 7);
        BalancedPathFilter balancedPathFilter = new BalancedPathFilter(random, extensions, parentPathLabelGenerator, 0, 4, 0, 1);
        BalancedPathFilter balancedPathFilter2 = new BalancedPathFilter(random, extensions, patternPathLabelGenerator, 0, 4, 0, 1);

        InputSplit[] samples = split.sample(randomPathFilter);
        assertEquals(1, samples.length);
        assertEquals(11, samples[0].length());

        InputSplit[] samples2 = split.sample(randomPathFilter2);
        assertEquals(1, samples2.length);
        assertEquals(7, samples2[0].length());

        InputSplit[] samples3 = split.sample(balancedPathFilter, 80, 20);
        assertEquals(2, samples3.length);
        assertEquals(3, samples3[0].length());
        assertEquals(1, samples3[1].length());

        InputSplit[] samples4 = split.sample(balancedPathFilter2, 50, 50);
        assertEquals(2, samples4.length);
        assertEquals(1, samples4[0].length());
        assertEquals(1, samples4[1].length());
    }
}
