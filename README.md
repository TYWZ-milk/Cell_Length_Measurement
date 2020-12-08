# Cell_Length_Measurement
wustl-CSE556A final project.

Measuring length of sperm cells of fruit flies.

## What required/wish-list features I have accomplished

### Required features
1. A GUI interface:

   Input: image
   
   Output: visualizes the traced cell; reports cell length (in micrometer; 3.06 pixels/micrometer)

    How to accomplish: Users can upload an image. After a series of operations, it will output an image which visualizes the traced cell and also reports cell length.
    You can see the output image in the left of the interface and cell length at the bottom of the output image.
    
2. Allows moderate user interaction.

   How to accomplish: I provide 8 buttons and 1 input field for users in the interface. Users can do such interactions with my interface:
   
   * Users can upload images.
   * Users can increase the contrast of the image.
   * Users can draw a rectangle or polygon as the outline of the cell on the image.
   * Users can undo what they did.
   * Users can input how many largest components they want.
   * Users can clear the canvas.
   * Users can draw points on the canvas by clicking the image.
   
3. Small deviation from ground truth Works for all easy examples.

    How to accomplish: We have three easy examples. All deviations are below 5%. And the deviation of 24708.1_2 at 20X.jpg is only 0.06%.
    
    File Name | 24708.1_1 at 20X.jpg | 24708.1_2 at 20X.jpg | 24708.1_3 at 20X.jpg |
    --- | --- | --- | --- |
    My best result | 2030 | 1786 | 1842 | 
    Ground truth | 1951 | 1787 | 1786 | 
    Deviation | 4.05% | 0.06% | 3.14% | 
    
### Wish-list features:

1. Small deviation from ground truth Works for most of the medium examples, as many of the hard examples as possible.

    How to accomplish:

    For all medium examples, most deviations are below 20%. Only the deviation of 24708.1_6 at 20X.jpg is more than 20%. The best one is 24708.1_4 at 20X.jpg which only has 0.18% deviation.

    Medium:
     
    File Name | 24708.1_4 at 20X.jpg | 24708.1_5 at 20X.jpg | 24708.1_6 at 20X.jpg | WT.C.1_20x.jpg | WT.C.2_20x.jpg |
    --- | --- | --- | --- | --- | --- |
My best result | 1678 | 2213 | 2587 | 978 | 2178 |
Ground truth | 1681 | 1952 | 1991 | 1090 | 1847 |
Deviation | -0.18% | 13.37% | 29.93% | -10.28% | 17.92% |

   Deviations of most hard examples are below 20%. The best one is 6.17%. But there are three hard examples which don't have a good result. The results of those three images don't have good matches with original cells. And the lengths are too large. 
    
   Hard:

    File Name | 28369.2.6_2.jpg | 28369.2.6_3.jpg | 472.1A.1_5 | 472.1A.1_4 | LHM.1B.3_2&3 | LHM.1B.3_7 | 472.1B.1_5&6 | 53387.1B.2_7&8 | 472.1A.1_2.jpg | 42568.b4.7 | 472.1A.1_3 |
    --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | 
My best result | 2000 | 2070 | 1680 | 1541 | 1733 | 1571 | 2061 | 2088 | bad result | bad result | bad result |
Ground truth | 1798.116 | 1820.409 | 1827.506 | 1836.393 | 1847 | 1849.383 | 1870.82 | 1873.806 | 1721.22 | 1840.172 | 1849.996 |
Deviation | 11.23% | 13.74% | -7.94% | -16.07% | -6.17% | -15.05% | 10.17% | 11.43% | N/A | N/A | N/A |

2. Users can draw points on images. They can use these points to build lines.

    How to accomplish: Users can draw points on images and they can click "complete" button to draw lines between these points automatically.
    But users can't change the color and width of lines.
    
3. GUI interface has some helpful buttons like redo button, eraser button.

    How to accomplish: There are five buttons to help users draw the interest-range.
    * Draw a rectangle: After clicking this button, users can draw two points by clicking on the image. After clicking the "complete" button, it will draw a rectangle on the image. Those two points are diagonal points of the rectangle.
    * Draw a polygon: After clicking this button, users can draw as many points as they want by clicking on the image.  After clicking the "complete" button, it will draw a polygon on the image. Those points are points of the polygon.
    * Undo: If users want to delete points what they drew before, they can click "undo" button. It will undo what you did.
    * Complete: After drawing points on the image, users can click this button to draw lines automatically.
    * Erase: After clicking this button, the canvas will be cleared.
    
## Description of core algorithms
The core algorithms are consist of 7 algorithms.
* Adaptive threshold by mean value.
* Label components.
* Get k largest components.
* Dilate and erode.
* Build cell complex.
* Thin algorithm.
* Process the outline of the final result.

The `Label components`, `Get k largest components`, `Dilate and erode`, `Build cell complex` and `Thin algorithm` are what we learned in class. I just made some small changes to these algorithms to get the best result.
For example, I only count components larger than 400 pixels in size. And I used dilate algorithm three times and erode algorithm 1 time.

The most significant coding component is `Adaptive threshold by mean value` and `Process the outline of the final result`.

### Adaptive threshold by mean value
In order to process the image, the first step is always threshold the image. At first, my threshold algorithm is what I learned from the class. But it didn't work for some images. These images are unevenly distributed in brightness.
