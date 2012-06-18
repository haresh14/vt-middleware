/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap.pool;

import java.io.InputStream;
import javax.naming.NamingException;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapConfig;

/**
 * <code>DefaultLdapFactory</code> provides a simple implementation of an ldap
 * factory.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DefaultLdapFactory extends AbstractLdapFactory<Ldap>
{

  /** Ldap config to create ldap objects with. */
  private LdapConfig config;

  /** Whether to connect to the ldap on object creation. */
  private boolean connectOnCreate = true;


  /**
   * This creates a new <code>DefaultLdapFactory</code> with the default
   * properties file, which must be located in your classpath.
   */
  public DefaultLdapFactory()
  {
    this.config = LdapConfig.createFromProperties(null);
    this.config.makeImmutable();
  }


  /**
   * This creates a new <code>DefaultLdapFactory</code> with the supplied input
   * stream.
   *
   * @param  is  <code>InputStream</code>
   */
  public DefaultLdapFactory(final InputStream is)
  {
    this.config = LdapConfig.createFromProperties(is);
    this.config.makeImmutable();
  }


  /**
   * This creates a new <code>DefaultLdapFactory</code> with the supplied ldap
   * configuration. The ldap configuration will be marked as immutable by this
   * factory.
   *
   * @param  lc  ldap config
   */
  public DefaultLdapFactory(final LdapConfig lc)
  {
    this.config = lc;
    this.config.makeImmutable();
  }


  /**
   * Returns whether ldap objects will attempt to connect after creation.
   * Default is true.
   *
   * @return  <code>boolean</code>
   */
  public boolean getConnectOnCreate()
  {
    return this.connectOnCreate;
  }


  /**
   * This sets whether newly created ldap objects will attempt to connect.
   * Default is true.
   *
   * @param  b  connect on create
   */
  public void setConnectOnCreate(final boolean b)
  {
    this.connectOnCreate = b;
  }


  /** {@inheritDoc} */
  public Ldap create()
  {
    Ldap l = new Ldap(this.config);
    if (this.connectOnCreate) {
      try {
        l.connect();
      } catch (NamingException e) {
        if (this.logger.isErrorEnabled()) {
          this.logger.error("unabled to connect to the ldap", e);
        }
        l = null;
      }
    }
    return l;
  }


  /** {@inheritDoc} */
  public void destroy(final Ldap l)
  {
    l.close();
    if (this.logger.isTraceEnabled()) {
      this.logger.trace("destroyed ldap object: " + l);
    }
  }
}
