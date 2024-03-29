/*
  $Id$

  Copyright (C) 2003-2008 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

import edu.vt.middleware.ldap.props.LdapProperties;
import edu.vt.middleware.ldap.props.PropertyInvoker;

/**
 * <code>AuthenticatorConfig</code> contains all the configuration data that the
 * <code>Authenticator</code> needs to control authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class AuthenticatorConfig extends LdapConfig
{

  /** Domain to look for ldap properties in, value is {@value}. */
  public static final String PROPERTIES_DOMAIN = "edu.vt.middleware.ldap.auth.";

  /** Invoker for ldap properties. */
  private static final PropertyInvoker PROPERTIES = new PropertyInvoker(
    AuthenticatorConfig.class,
    PROPERTIES_DOMAIN);

  /** Directory user field. */
  private String[] userField = new String[] {
    LdapConstants.DEFAULT_USER_FIELD,
  };

  /** User to authenticate. */
  private String user;

  /** Credential for authenticating user. */
  private Object credential;

  /** Filter for authorizing user. */
  private String authorizationFilter;

  /** Whether to construct the DN when authenticating. */
  private boolean constructDn = LdapConstants.DEFAULT_CONSTRUCT_DN;

  /** Whether to perform subtree searches for DNs. */
  private boolean subtreeSearch = LdapConstants.DEFAULT_SUBTREE_SEARCH;


  /** Default constructor. */
  public AuthenticatorConfig() {}


  /**
   * This will create a new <code>AuthenticatorConfig</code> with the supplied
   * ldap url and base Strings.
   *
   * @param  ldapUrl  <code>String</code> LDAP URL
   * @param  base  <code>String</code> LDAP base DN
   */
  public AuthenticatorConfig(final String ldapUrl, final String base)
  {
    this();
    this.setLdapUrl(ldapUrl);
    this.setBase(base);
  }


  /**
   * This returns the user field(s) of the <code>Authenticator</code>.
   *
   * @return  <code>String[]</code> - user field name(s)
   */
  public String[] getUserField()
  {
    return this.userField;
  }


  /**
   * This returns the user of the <code>Authenticator</code>.
   *
   * @return  <code>String</code> - user name
   */
  public String getUser()
  {
    return this.user;
  }


  /**
   * This returns the credential of the <code>Authenticator</code>.
   *
   * @return  <code>Object</code> - user credential
   */
  public Object getCredential()
  {
    return this.credential;
  }


  /**
   * This returns the filter used to authorize users.
   *
   * @return  <code>String</code> - filter
   */
  public String getAuthorizationFilter()
  {
    return this.authorizationFilter;
  }


  /**
   * This returns the constructDn of the <code>Authenticator</code>.
   *
   * @return  <code>boolean</code> - whether the DN will be constructed
   */
  public boolean getConstructDn()
  {
    return this.constructDn;
  }


  /**
   * This returns the subtreeSearch of the <code>Authenticator</code>.
   *
   * @return  <code>boolean</code> - whether the DN will be searched for over
   * the entire base
   */
  public boolean getSubtreeSearch()
  {
    return this.subtreeSearch;
  }


  /**
   * This sets the user fields for the <code>Authenticator</code>. The user
   * field is used to lookup a user's dn.
   *
   * @param  userField  <code>String[]</code> username
   */
  public void setUserField(final String[] userField)
  {
    checkImmutable();
    this.userField = userField;
  }


  /**
   * This sets the username for the <code>Authenticator</code> to use for
   * authentication.
   *
   * @param  user  <code>String</code> username
   */
  public void setUser(final String user)
  {
    checkImmutable();
    this.user = user;
  }

  /**
   * This sets the credential for the <code>Authenticator</code> to use for
   * authentication.
   *
   * @param  credential  <code>Object</code>
   */
  public void setCredential(final Object credential)
  {
    checkImmutable();
    this.credential = credential;
  }


  /**
   * This sets the filter used to authorize users. If not set, no authorization
   * is performed.
   *
   * @param  authorizationFilter  <code>String</code>
   */
  public void setAuthorizationFilter(final String authorizationFilter)
  {
    checkImmutable();
    this.authorizationFilter = authorizationFilter;
  }


  /**
   * This sets the constructDn for the <code>Authenticator</code>. If true, the
   * DN used for authenticating will be constructed using the {@link #userField}
   * and {@link LdapConfig#getBase()}. In the form: dn =
   * userField+'='+user+','+base Otherwise the DN will be looked up in the LDAP.
   *
   * @param  constructDn  <code>boolean</code>
   */
  public void setConstructDn(final boolean constructDn)
  {
    checkImmutable();
    this.constructDn = constructDn;
  }


  /**
   * This sets the subtreeSearch for the <code>Authenticator</code>. If true,
   * the DN used for authenticating will be searched for over the entire {@link
   * LdapConfig#getBase()}. Otherwise the DN will be search for in the {@link
   * LdapConfig#getBase()} context.
   *
   * @param  subtreeSearch  <code>boolean</code>
   */
  public void setSubtreeSearch(final boolean subtreeSearch)
  {
    checkImmutable();
    this.subtreeSearch = subtreeSearch;
  }


  /** {@inheritDoc}. */
  public String getPropertiesDomain()
  {
    return PROPERTIES_DOMAIN;
  }


  /** {@inheritDoc}. */
  public void setEnvironmentProperties(final String name, final String value)
  {
    checkImmutable();
    if (name != null && value != null) {
      if (PROPERTIES.hasProperty(name)) {
        PROPERTIES.setProperty(this, name, value);
      } else {
        super.setEnvironmentProperties(name, value);
      }
    }
  }


  /** {@inheritDoc}. */
  public boolean hasEnvironmentProperty(final String name)
  {
    return PROPERTIES.hasProperty(name);
  }


  /**
   * Create an instance of this class initialized with properties from the
   * properties file. If propertiesFile is null, load properties from the
   * default properties file.
   *
   * @param  propertiesFile  to load properties from
   *
   * @return  <code>AuthenticatorConfig</code> initialized ldap pool config
   */
  public static AuthenticatorConfig createFromProperties(
    final String propertiesFile)
  {
    final AuthenticatorConfig authConfig = new AuthenticatorConfig();
    LdapProperties properties = null;
    if (propertiesFile != null) {
      properties = new LdapProperties(authConfig, propertiesFile);
    } else {
      properties = new LdapProperties(authConfig);
    }
    properties.configure();
    return authConfig;
  }
}
