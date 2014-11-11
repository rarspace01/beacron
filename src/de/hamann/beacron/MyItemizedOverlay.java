package de.hamann.beacron;

import java.util.ArrayList;
import java.util.Iterator;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * @author PaulB
 */
public class MyItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {
        private ArrayList<OverlayItem> mItemList = new ArrayList<OverlayItem>();

        public MyItemizedOverlay(ArrayList<OverlayItem> pList, ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener, ResourceProxy pResourceProxy) {
                super(pList, pOnItemGestureListener, pResourceProxy);
                mItemList = pList;
                populateAll();
        }

        public void addOverlay(OverlayItem aOverlayItem) {
                mItemList.add(aOverlayItem);
        }

        public void removeOverlay(OverlayItem aOverlayItem) {
                mItemList.remove(aOverlayItem);
        }

        // only really used and new method here
        // removes a overlay by its title
        public void removeOverlayByTitle(String title) {
                Iterator<OverlayItem> iterator = mItemList.iterator();
                while (iterator.hasNext()) {
                        OverlayItem curItem = iterator.next();
                        if (curItem.getTitle().equals(title)) {
                                iterator.remove();
                                break;
                        }
                }
                populateAll();
        }

        public void populateAll() {
                populate();
        }
}