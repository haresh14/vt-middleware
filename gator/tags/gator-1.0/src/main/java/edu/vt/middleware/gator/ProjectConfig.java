/*
  $Id$

  Copyright (C) 2008 Virginia Tech, Marvin S. Addison.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Marvin S. Addison
  Email:   serac@vt.edu
  Version: $Revision$
  Updated: $Date$
 */
package edu.vt.middleware.gator;

import java.util.Calendar;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Project is the top-level class in the configuration hierarchy.
 *
 * @author Marvin S. Addison
 *
 */
@Entity
@Table(name = "log_projects")
@SequenceGenerator(
  name = "project_sequence",
  sequenceName = "log_seq_projects",
  allocationSize = 1)
public class ProjectConfig extends Config
{
  /** Hash code seed */
  private static final int HASH_CODE_SEED = 1024;
 
  /** Log directory prepended to file appender paths used by client */
  private String clientLogDir;
 
  /** Date/time of most recent modification to this project configuration */
  private Calendar modifiedDate;
 
  /** Clients allowed to use this project configuration */
  private Set<ClientConfig> clients;

  /** Logger categories defined in this project */
  private Set<CategoryConfig> categories;
 
  /** Appenders defined in this project */
  private Set<AppenderConfig> appenders;
  

  /** {@inheritDoc} */
  @Id
  @Column(name = "project_id", nullable = false)
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "project_sequence")
  public int getId()
  {
    return id;
  }


  /**
   * Gets the date/time the project configuration was last modified.
   * @return Modification date.
   */
  @Column(name = "modified_date", nullable = false)
  public Calendar getModifiedDate()
  {
    return modifiedDate;
  }

  /**
   * Sets the date/time of the last project modification.
   * @param date Modification date.
   */
  public void setModifiedDate(final Calendar date)
  {
    this.modifiedDate = date;
  }

  /**
   * Gets the log directory prepended to client appender paths.
   * @return Absolute path to root of client logs.
   */
  @Column(name = "client_log_dir")
  public String getClientLogDir()
  {
    return clientLogDir;
  }

  /**
   * Sets the log directory prepended to client appender paths.
   * @param dir Absolute path to root of client logs.
   */
  public void setClientLogDir(final String dir)
  {
    this.clientLogDir = dir;
  }
  
  /**
   * Gets the collection of clients belonging to this project.
   * @return Clients of this project.
   */
  @OneToMany(
    mappedBy = "project",
    cascade = CascadeType.ALL)
  protected Set<ClientConfig> getClientsInternal()
  {
    if (clients == null) {
      clients = new HashSet<ClientConfig>();
    }
    return clients;
  }
  
  /**
   * Sets the collection of clients belonging to this project.
   * @param s Clients of this project.
   */
  protected void setClientsInternal(final Set<ClientConfig> s)
  {
    clients = s;
  }

  /**
   * Gets an immutable collection of clients belonging to this project.
   * Clients are sorted by name in the returned collection.
   * @return Clients of this project.
   */
  @Transient
  public Collection<ClientConfig> getClients()
  {
    final SortedSet<ClientConfig> clientSet =
      new TreeSet<ClientConfig>(new ConfigComparator());
    clientSet.addAll(getClientsInternal());
    return Collections.unmodifiableCollection(clientSet);
  }
 
  /**
   * Gets the client with the given ID belonging to this project.
   * @param clientId Client ID.
   * @return Client in this project with matching ID or null if no matching
   * client is found.
   */
  @Transient
  public ClientConfig getClient(final int clientId)
  {
    for (ClientConfig client : getClientsInternal()) {
      if (client.getId() == clientId) {
        return client;
      }
    }
    return null;
  }
  
  /**
   * Gets a client by name belonging to this project.  Name comparison is case
   * sensitive.
   * @param name Client name.
   * @return Client in this project with matching name or null if no matching
   * client is found.
   */
  @Transient
  public ClientConfig getClient(final String name)
  {
    for (ClientConfig client : getClientsInternal()) {
      if (name != null && client.getName().equals(name)) {
        return client;
      }
    }
    return null;
  }

  /**
   * Adds a client to this project.
   * Client names must be unique within a project.
   * @param Client to add.
   */
  public void addClient(final ClientConfig client)
  {
    if (getClient(client.getName()) != null) {
      throw new IllegalArgumentException(
        "Client names must be unique within a project.");
    }
    client.setProject(this);
    getClientsInternal().add(client);
  }
  
  /**
   * Removes a client from this project.
   * @param client To be removed.
   */
  public void removeClient(final ClientConfig client)
  {
    client.setProject(null);
    getClientsInternal().remove(client);
  }

  
  /**
   * Gets the collection of appenders belonging to this project.
   * @return appenders of this project.
   */
  @OneToMany(
    mappedBy = "project",
    cascade = CascadeType.ALL)
  protected Set<AppenderConfig> getAppendersInternal()
  {
    if (appenders == null) {
      appenders = new HashSet<AppenderConfig>();
    }
    return appenders;
  }
  
  /**
   * Sets the collection of appenders belonging to this project.
   * @param s appenders of this project.
   */
  protected void setAppendersInternal(final Set<AppenderConfig> s)
  {
    appenders = s;
  }

  /**
   * Gets an immutable collection of appenders belonging to this project.
   * Appenders are sorted by name in the returned collection.
   * @return Appenders of this project.
   */
  @Transient
  public Collection<AppenderConfig> getAppenders() {
    final SortedSet<AppenderConfig> appenderSet =
      new TreeSet<AppenderConfig>(new ConfigComparator());
    appenderSet.addAll(getAppendersInternal());
    return Collections.unmodifiableCollection(appenderSet);
  }
 
  /**
   * Gets the appender with the given ID belonging to this project.
   * @param appenderId Appender ID.
   * @return Appender in this project with matching ID or null if no matching
   * appender is found.
   */
  @Transient
  public AppenderConfig getAppender(final int appenderId)
  {
    for (AppenderConfig appender : getAppendersInternal()) {
      if (appender.getId() == appenderId) {
        return appender;
      }
    }
    return null;
  }
  
  /**
   * Gets the appender with the given name belonging to this project.
   * @param name Appender name.
   * @return Appender in this project with matching name or null if no matching
   * appender is found.
   */
  @Transient
  public AppenderConfig getAppender(final String name)
  {
    for (AppenderConfig appender : getAppendersInternal()) {
      if (name != null && appender.getName().equals(name)) {
        return appender;
      }
    }
    return null;
  }

  /**
   * Adds an appender to this project.
   * Appender names must be unique within a project.
   * @param Appender to add.
   */
  public void addAppender(final AppenderConfig appender)
  {
    if (getAppender(appender.getName()) != null) {
      throw new IllegalArgumentException(
        "Appender names must be unique within a project.");
    }
    appender.setProject(this);
    getAppendersInternal().add(appender);
  }
  
  /**
   * Removes an appender from this project.
   * @param appender To be removed.
   */
  public void removeAppender(final AppenderConfig appender)
  {
    appender.setProject(null);
    getAppendersInternal().remove(appender);
  }

  
  /**
   * Gets the collection of categories belonging to this project.
   * @return Categories of this project.
   */
  @OneToMany(
    mappedBy = "project",
    cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
  protected Set<CategoryConfig> getCategoriesInternal()
  {
    if (categories == null) {
      categories = new HashSet<CategoryConfig>();
    }
    return categories;
  }
  
  /**
   * Sets the collection of categories belonging to this project.
   * @param s Categories of this project.
   */
  protected void setCategoriesInternal(final Set<CategoryConfig> s)
  {
    categories = s;
  }

  /**
   * Gets an immutable collection of categories belonging to this project.
   * Categories are sorted by name in the returned collection.
   * @return Categories of this project.
   */
  @Transient
  public Collection<CategoryConfig> getCategories()
  {
    final SortedSet<CategoryConfig> categorySet =
      new TreeSet<CategoryConfig>(new ConfigComparator());
    categorySet.addAll(getCategoriesInternal());
    return Collections.unmodifiableCollection(categorySet);
  }
  
  /**
   * Gets the category with the given ID belonging to this project.
   * @param categoryId Category ID.
   * @return Category in this project with matching ID or null if no matching
   * category is found.
   */
  @Transient
  public CategoryConfig getCategory(final int categoryId)
  {
    for (CategoryConfig category : getCategoriesInternal()) {
      if (category.getId() == categoryId) {
        return category;
      }
    }
    return null;
  }
  
  /**
   * Gets the category with the given name belonging to this project.
   * @param name Category name.
   * @return Category in this project with matching name or null if no matching
   * category is found.
   */
  @Transient
  public CategoryConfig getCategory(final String name)
  {
    for (CategoryConfig category : getCategoriesInternal()) {
      if (name != null && category.getName().equals(name)) {
        return category;
      }
    }
    return null;
  }

  /**
   * Adds an category to this project.
   * Category names must be unique within a project.
   * @param Category to add.
   */
  public void addCategory(final CategoryConfig category)
  {
    if (getCategory(category.getName()) != null) {
      throw new IllegalArgumentException(
        "Category names must be unique within a project.");
    }
    category.setProject(this);
    getCategoriesInternal().add(category);
  }
  
  /**
   * Removes an category from this project.
   * @param category To be removed.
   */
  public void removeCategory(final CategoryConfig category)
  {
    category.setProject(null);
    getCategoriesInternal().remove(category);
  }

  /** {@inheritDoc} */
  @Transient
  protected int getHashCodeSeed()
  {
    return HASH_CODE_SEED;
  }
}
