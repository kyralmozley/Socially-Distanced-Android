package space.sociallydistanced;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

class MyBarDataSet extends BarDataSet {

    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    /**
     * Define the colours for the bar in bar chart
     * this gives the nice color difference depending on Y value
     * @param index data point entry
     * @return int of colour
     */
    @Override
    public int getColor(int index) {
        if(getEntryForIndex(index).getY() > 80)
            return mColors.get(0);
        else if(getEntryForIndex(index).getY() > 70)
            return mColors.get(1);
        else if(getEntryForIndex(index).getY() > 60)
            return mColors.get(2);
        else if(getEntryForIndex(index).getY() > 50)
            return  mColors.get(3);
        else if(getEntryForIndex(index).getY() > 40)
            return mColors.get(4);
        else if(getEntryForIndex(index).getY() > 30)
            return mColors.get(5);
        else if(getEntryForIndex(index).getY() > 20)
            return mColors.get(6);
        else if(getEntryForIndex(index).getY() > 10)
            return mColors.get(7);
        else
            return mColors.get(8);
    }
}