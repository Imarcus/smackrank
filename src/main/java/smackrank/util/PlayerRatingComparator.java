package smackrank.util;

import smackrank.model.Rated;

import java.util.Comparator;

public class PlayerRatingComparator implements Comparator<Rated> {
    @Override
    public int compare(Rated o1, Rated o2) {
        return o1.getRating() - o2.getRating();
    }
}
