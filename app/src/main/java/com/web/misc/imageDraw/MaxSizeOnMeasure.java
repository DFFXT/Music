package com.web.misc.imageDraw;

import android.view.View;

import org.jetbrains.annotations.NotNull;

public class MaxSizeOnMeasure implements IOnMeasure {
    private int maxWidth;
    private int maxHeight;

    public MaxSizeOnMeasure(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    @Override
    public void onMeasure(@NotNull int[] measure) {
        if(maxWidth>0){
            int width= Math.min(View.MeasureSpec.getSize(measure[0]),maxWidth);
            int widthMode= View.MeasureSpec.getMode(measure[0]);
            measure[0]= View.MeasureSpec.makeMeasureSpec(width,widthMode);
        }
        if(maxHeight>0){
            int height = Math.min(View.MeasureSpec.getSize(measure[1]),maxHeight);
            int heightMode= View.MeasureSpec.getMode(measure[1]);
            measure[1]= View.MeasureSpec.makeMeasureSpec(height,heightMode);
        }

    }
}
