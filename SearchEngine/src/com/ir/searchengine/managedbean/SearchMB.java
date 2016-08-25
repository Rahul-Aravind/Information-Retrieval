package com.ir.searchengine.managedbean;

import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@ManagedBean
public class SearchMB {
	
	private String searchQuery;

	/**
	 * @return the searchQuery
	 */
	public String getSearchQuery() {
		return searchQuery;
	}

	public SearchMB() {
		super();
	}

	/**
	 * @param searchQuery the searchQuery to set
	 */
	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	public String performSearch()
	{
		try
		{
			FacesContext facesContext=FacesContext.getCurrentInstance();
			ExternalContext externalContext=facesContext.getExternalContext();
			HttpSession httpSession=(HttpSession) externalContext.getSession(true);
			httpSession.setAttribute("query",this.getSearchQuery());
			System.out.println("Going to next page");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}

}
