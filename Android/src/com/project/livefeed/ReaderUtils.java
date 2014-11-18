package com.project.livefeed;

import com.thingmagic.Reader;
import com.thingmagic.TMConstants;

public class ReaderUtils {
	
	public static Reader reader = null;
	
	public static void connect(String uriString) throws Exception {
		Reader readertmp = null;
		
		try {
			readertmp = Reader.create(uriString);
			readertmp.connect();
			
			if (Reader.Region.UNSPEC == (Reader.Region) readertmp.paramGet("/reader/region/id")){
				Reader.Region[] supportedRegions = (Reader.Region[]) readertmp
						.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
				
				if (supportedRegions.length < 1) {
					throw new Exception("Reader doesn't support any regions");
				} 
				else {
					readertmp.paramSet("/reader/region/id", supportedRegions[0]);
				}
			}
		}catch(Exception ex){
			throw ex;
		}
		
		reader = readertmp;
	}
}