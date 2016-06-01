package org.aroundthecode.pathfinder.server.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class FilterItemTest {

	@Test
	public void testDefaultFilterItem() {
		FilterItem f = new FilterItem();
		assertNotNull(f);
		assertEquals( FilterItem.DEFAULT, f.getFilterGN1());
		assertEquals( FilterItem.DEFAULT, f.getFilterAN1());
		assertEquals( FilterItem.DEFAULT, f.getFilterPN1());
		assertEquals( FilterItem.DEFAULT, f.getFilterCN1());
		assertEquals( FilterItem.DEFAULT, f.getFilterVN1());
		
		assertEquals( FilterItem.DEFAULT, f.getFilterGN2());
		assertEquals( FilterItem.DEFAULT, f.getFilterAN2());
		assertEquals( FilterItem.DEFAULT, f.getFilterPN2());
		assertEquals( FilterItem.DEFAULT, f.getFilterCN2());
		assertEquals( FilterItem.DEFAULT, f.getFilterVN2());
	}
	
	@Test
	public void testFilterItem() {
		FilterItem f = new FilterItem("g1","a1","p1","c1","v1","g2","a2","p2","c2","v2");
		assertNotNull(f);
		assertEquals( "g1", f.getFilterGN1());
		assertEquals( "a1", f.getFilterAN1());
		assertEquals( "p1", f.getFilterPN1());
		assertEquals( "c1", f.getFilterCN1());
		assertEquals( "v1", f.getFilterVN1());
		
		assertEquals( "g2", f.getFilterGN2());
		assertEquals( "a2", f.getFilterAN2());
		assertEquals( "p2", f.getFilterPN2());
		assertEquals( "c2", f.getFilterCN2());
		assertEquals( "v2", f.getFilterVN2());
	}

}
