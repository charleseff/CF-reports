package com.cfinkel.reports;

import java.util.List;
import java.util.Map;

/**
 * $Author$
 * $Revision$
 * $Date$
 * <p/>
 * created:
 * User: charles
 * Date: Jan 26, 2006
 * Time: 2:54:19 AM
 */
public interface PostQueryProcessor {

    public List<Map> postProcess(List<Map> data);
    
}
