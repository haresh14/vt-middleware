/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive;

import java.util.Arrays;
import org.ldaptive.control.ResponseControl;

/**
 * Wrapper class for all operation responses.
 *
 * @param  <T>  type of ldap result contained in this response
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class Response<T> implements Message<ResponseControl>
{

  /** Operation response. */
  private final T result;

  /** Operation result code. */
  private final ResultCode resultCode;

  /** Response message. */
  private final String message;

  /** Response matched DN. */
  private final String matchedDn;

  /** Response controls. */
  private final ResponseControl[] responseControls;

  /** Referral URLs. */
  private final String[] referralURLs;


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   * @param  rc  result code
   */
  public Response(final T t, final ResultCode rc)
  {
    result = t;
    resultCode = rc;
    message = null;
    matchedDn = null;
    responseControls = null;
    referralURLs = null;
  }


  /**
   * Creates a new ldap response.
   *
   * @param  t  response type
   * @param  rc  result code
   * @param  msg  result message
   * @param  dn  matched dn
   * @param  c  response controls
   * @param  urls  referral urls
   */
  public Response(
    final T t,
    final ResultCode rc,
    final String msg,
    final String dn,
    final ResponseControl[] c,
    final String[] urls)
  {
    result = t;
    resultCode = rc;
    message = msg;
    matchedDn = dn;
    responseControls = c;
    referralURLs = urls;
  }


  /**
   * Returns the result of the ldap operation.
   *
   * @return  operation result
   */
  public T getResult()
  {
    return result;
  }


  /**
   * Returns the result code of the ldap operation.
   *
   * @return  operation result code
   */
  public ResultCode getResultCode()
  {
    return resultCode;
  }


  /**
   * Returns any error or diagnostic message produced by the ldap operation.
   *
   * @return  message
   */
  public String getMessage()
  {
    return message;
  }


  /**
   * Returns the matched DN produced by the ldap operation.
   *
   * @return  matched DN
   */
  public String getMatchedDn()
  {
    return matchedDn;
  }


  /** {@inheritDoc} */
  @Override
  public ResponseControl[] getControls()
  {
    return responseControls;
  }


  /**
   * Returns the referral URLs produced by the ldap operation.
   *
   * @return  referral urls
   */
  public String[] getReferralURLs()
  {
    return referralURLs;
  }


  /** {@inheritDoc} */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::result=%s, resultCode=%s, message=%s, matchedDn=%s, " +
        "responseControls=%s, referralURLs=%s]",
        getClass().getName(),
        hashCode(),
        result,
        resultCode,
        message,
        matchedDn,
        Arrays.toString(responseControls),
        Arrays.toString(referralURLs));
  }
}
