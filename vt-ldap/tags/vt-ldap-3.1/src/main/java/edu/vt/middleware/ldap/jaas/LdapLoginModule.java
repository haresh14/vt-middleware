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
package edu.vt.middleware.ldap.jaas;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import com.sun.security.auth.callback.TextCallbackHandler;
import edu.vt.middleware.ldap.Authenticator;

/**
 * <code>LdapLoginModule</code> provides a JAAS authentication hook into LDAP
 * authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapLoginModule extends AbstractLoginModule implements LoginModule
{

  /** User attribute to add to role data. */
  private String[] userRoleAttribute;

  /** Authenticator to use against the LDAP. */
  private Authenticator auth;


  /** {@inheritDoc}. */
  public void initialize(
    final Subject subject,
    final CallbackHandler callbackHandler,
    final Map<String, ?> sharedState,
    final Map<String, ?> options)
  {
    this.setLdapPrincipal = true;
    this.setLdapDnPrincipal = true;
    this.setLdapCredential = true;

    super.initialize(subject, callbackHandler, sharedState, options);

    final Iterator<String> i = options.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      final String value = (String) options.get(key);
      if (key.equalsIgnoreCase("userRoleAttribute")) {
        this.userRoleAttribute = value.split(",");
      }
    }

    if (this.logger.isDebugEnabled()) {
      this.logger.debug(
        "userRoleAttribute = " +
        (this.userRoleAttribute == null
          ? "null" : Arrays.asList(this.userRoleAttribute)));
    }

    this.auth = createAuthenticator(options);
    if (this.logger.isDebugEnabled()) {
      this.logger.debug(
        "Created authenticator: " + this.auth.getAuthenticatorConfig());
    }
  }


  /** {@inheritDoc}. */
  public boolean login()
    throws LoginException
  {
    try {
      final NameCallback nameCb = new NameCallback("Enter user: ");
      final PasswordCallback passCb = new PasswordCallback(
        "Enter user password: ",
        false);
      this.getCredentials(nameCb, passCb, false);

      final List<LdapRole> roles = new ArrayList<LdapRole>();
      try {
        final Attributes attrs = this.auth.authenticate(
          nameCb.getName(),
          passCb.getPassword(),
          this.userRoleAttribute);
        roles.addAll(this.attributesToRoles(attrs));
        this.success = true;
      } catch (AuthenticationException e) {
        if (this.tryFirstPass) {
          this.getCredentials(nameCb, passCb, true);
          try {
            final Attributes attrs = this.auth.authenticate(
              nameCb.getName(),
              passCb.getPassword(),
              this.userRoleAttribute);
            roles.addAll(this.attributesToRoles(attrs));
            this.success = true;
          } catch (AuthenticationException e2) {
            this.success = false;
          }
        } else {
          this.success = false;
        }
      }
      if (!this.success) {
        throw new LoginException("Authentication failed.");
      } else {
        if (this.setLdapPrincipal) {
          this.principals.add(new LdapPrincipal(nameCb.getName()));
        }

        final String loginDn = this.auth.getDn(nameCb.getName());
        if (loginDn != null && this.setLdapDnPrincipal) {
          this.principals.add(new LdapDnPrincipal(loginDn));
        }
        if (this.setLdapCredential) {
          this.credentials.add(new LdapCredential(passCb.getPassword()));
        }
        if (!roles.isEmpty()) {
          this.principals.addAll(roles);
        }
        this.storeCredentials(nameCb, passCb, loginDn);
      }
    } catch (NamingException e) {
      this.success = false;
      throw new LoginException(e.toString());
    } finally {
      this.auth.close();
    }
    return true;
  }


  /**
   * This provides command line access to a <code>LdapLoginModule</code>.
   *
   * @param  args  <code>String[]</code>
   *
   * @throws  Exception  if an error occurs
   */
  public static void main(final String[] args)
    throws Exception
  {
    String name = "vt-ldap";
    if (args.length > 0) {
      name = args[0];
    }

    final LoginContext lc = new LoginContext(name, new TextCallbackHandler());
    lc.login();
    System.out.println("Authentication/Authorization succeeded");

    final Set<Principal> principals = lc.getSubject().getPrincipals();
    System.out.println("Subject Principal(s): ");

    final Iterator<Principal> i = principals.iterator();
    while (i.hasNext()) {
      final Principal p = i.next();
      System.out.println("  " + p.getName());
    }
    lc.logout();
  }
}
