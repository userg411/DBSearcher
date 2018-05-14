/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaclasses;

import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;


@ManagedBean(name = "mainBean")
@ViewScoped
public class MainBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @ManagedProperty("#{searchBeans}")
    private ArrayList <SearchBean> searchBeans;
    private int i = 1;

    public String getI() {
        return i+"";
    }

    public void setI(int i) {
        this.i = i;
    }
    
    public ArrayList<SearchBean> getSearchBeans() {
        return searchBeans;
    }

    public void setSearchBeans(ArrayList<SearchBean> searchBeans) {
        this.searchBeans = searchBeans;
    }
    
    
   
    
    @PostConstruct
    public void init(){
        searchBeans = new ArrayList<SearchBean>();
        searchBeans.add(new SearchBean());
        searchBeans.add(new SearchBean());
        
    }
    
    
}
