package com.web.misc.imageDraw;

import android.view.View;

import org.jetbrains.annotations.NotNull;

public class MinSizeOnMeasure implements IOnMeasure {
    private int minWidth;
    private int minHeight;

    public MinSizeOnMeasure(int minWidth, int minHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
    }

    @Override
    public void onMeasure(@NotNull int[] measure) {
        if(minWidth>0){
            int width= Math.max(View.MeasureSpec.getSize(measure[0]),minWidth);
            int widthMode= View.MeasureSpec.getMode(measure[0]);
            measure[0]= View.MeasureSpec.makeMeasureSpec(width,widthMode);
        }
        if(minHeight>0){
            int height = Math.max(View.MeasureSpec.getSize(measure[1]),minHeight);
            int heightMode= View.MeasureSpec.getMode(measure[1]);
            measure[1]= View.MeasureSpec.makeMeasureSpec(height,heightMode);
        }

    }
}
