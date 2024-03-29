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
package edu.vt.middleware.ldap.pool;

import javax.naming.NamingException;
import edu.vt.middleware.ldap.Ldap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>CompareLdapValidator</code> validates a ldap connection is healthy by
 * performing a compare operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class CompareLdapValidator implements LdapValidator<Ldap>
{

  /** Log for this class. */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /** DN for validating connections. */
  private String validateDn;

  /** Filter for validating connections. */
  private String validateFilter;


  /**
   * Creates a new <code>CompareLdapValiadtor</code> with the supplied compare
   * dn and filter.
   *
   * @param  dn  to use for compares
   * @param  filter  to use for compares
   */
  public CompareLdapValidator(final String dn, final String filter)
  {
    this.validateDn = dn;
    this.validateFilter = filter;
  }


  /** {@inheritDoc}. */
  public boolean validate(final Ldap l)
  {
    boolean success = false;
    if (l != null) {
      try {
        success = l.compare(this.validateDn, this.validateFilter);
      } catch (NamingException e) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug(
            "validation failed for compare " + this.validateFilter,
            e);
        }
      }
    }
    return success;
  }
}
