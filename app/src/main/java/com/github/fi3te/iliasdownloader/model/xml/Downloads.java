package com.github.fi3te.iliasdownloader.model.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wennier on 31.05.2015.
 */
public class Downloads {

    private List<String> pathList;

    public Downloads() {
        this.pathList = Collections.synchronizedList(new ArrayList<String>());
    }

    public Downloads(List<String> pathList) {
        this.pathList = Collections.synchronizedList(pathList);
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void deleteHistory() {
        pathList.clear();
    }
}
