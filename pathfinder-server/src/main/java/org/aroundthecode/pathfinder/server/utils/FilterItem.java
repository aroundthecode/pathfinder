package org.aroundthecode.pathfinder.server.utils;

public class FilterItem {

	protected final static String DEFAULT = ".*";
	
	private String filterGN1 = DEFAULT;
	private String filterAN1 = DEFAULT;
	private String filterPN1 = DEFAULT;
	private String filterCN1 = DEFAULT;
	private String filterVN1 = DEFAULT;
	
	private String filterGN2 = DEFAULT;
	private String filterAN2 = DEFAULT;
	private String filterPN2 = DEFAULT;
	private String filterCN2 = DEFAULT;
	private String filterVN2 = DEFAULT;

	public FilterItem() {
	}
	
	public FilterItem(String filterGN1, String filterAN1, String filterPN1, String filterCN1, String filterVN1, String filterGN2, String filterAN2, String filterPN2, String filterCN2, String filterVN2) {
		setFilterGN1(filterGN1);
		setFilterAN1(filterAN1);
		setFilterPN1(filterPN1);
		setFilterCN1(filterCN1);
		setFilterVN1(filterVN1);
		setFilterGN2(filterGN2);
		setFilterAN2(filterAN2);
		setFilterPN2(filterPN2);
		setFilterCN2(filterCN2);
		setFilterVN2(filterVN2);
	}
	
	public String getFilterGN1() {
		return filterGN1;
	}
	public void setFilterGN1(String filterGN1) {
		this.filterGN1 = filterGN1;
	}
	public String getFilterAN1() {
		return filterAN1;
	}
	public void setFilterAN1(String filterAN1) {
		this.filterAN1 = filterAN1;
	}
	public String getFilterPN1() {
		return filterPN1;
	}
	public void setFilterPN1(String filterPN1) {
		this.filterPN1 = filterPN1;
	}
	public String getFilterCN1() {
		return filterCN1;
	}
	public void setFilterCN1(String filterCN1) {
		this.filterCN1 = filterCN1;
	}
	public String getFilterVN1() {
		return filterVN1;
	}
	public void setFilterVN1(String filterVN1) {
		this.filterVN1 = filterVN1;
	}
	public String getFilterGN2() {
		return filterGN2;
	}
	public void setFilterGN2(String filterGN2) {
		this.filterGN2 = filterGN2;
	}
	public String getFilterAN2() {
		return filterAN2;
	}
	public void setFilterAN2(String filterAN2) {
		this.filterAN2 = filterAN2;
	}
	public String getFilterPN2() {
		return filterPN2;
	}
	public void setFilterPN2(String filterPN2) {
		this.filterPN2 = filterPN2;
	}
	public String getFilterCN2() {
		return filterCN2;
	}
	public void setFilterCN2(String filterCN2) {
		this.filterCN2 = filterCN2;
	}
	public String getFilterVN2() {
		return filterVN2;
	}
	public void setFilterVN2(String filterVN2) {
		this.filterVN2 = filterVN2;
	}

}
