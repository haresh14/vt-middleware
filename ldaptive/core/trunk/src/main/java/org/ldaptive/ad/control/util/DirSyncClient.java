/*
  $Id$

  Copyright (C) 2003-2014 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.ad.control.util;

import org.ldaptive.Connection;
import org.ldaptive.LdapException;
import org.ldaptive.Response;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.ad.control.DirSyncControl;
import org.ldaptive.ad.control.ExtendedDnControl;
import org.ldaptive.ad.control.ShowDeletedControl;
import org.ldaptive.control.RequestControl;
import org.ldaptive.control.util.CookieManager;
import org.ldaptive.control.util.DefaultCookieManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client that simplifies using the active directory dir sync control.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class DirSyncClient
{

  /** Logger for this class. */
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /** Connection to invoke the search operation on. */
  private final Connection connection;

  /** DirSync flags. */
  private final DirSyncControl.Flag[] dirSyncFlags;

  /** Maximum attribute count. */
  private final int maxAttributeCount;

  /** ExtendedDn flags. */
  private ExtendedDnControl.Flag extendedDnFlag =
    ExtendedDnControl.Flag.STANDARD;


  /**
   * Creates a new dir sync client.
   *
   * @param  conn  to execute the search operation on
   */
  public DirSyncClient(final Connection conn)
  {
    this(conn, null, 0);
  }


  /**
   * Creates a new dir sync client.
   *
   * @param  conn  to execute the search operation on
   * @param  dsFlags  to set on the dir sync control
   */
  public DirSyncClient(
    final Connection conn,
    final DirSyncControl.Flag[] dsFlags)
  {
    this(conn, dsFlags, 0);
  }


  /**
   * Creates a new dir sync client.
   *
   * @param  conn  to execute the search operation on
   * @param  dsFlags  to set on the dir sync control
   * @param  count  max attribute count
   */
  public DirSyncClient(
    final Connection conn,
    final DirSyncControl.Flag[] dsFlags,
    final int count)
  {
    connection = conn;
    dirSyncFlags = dsFlags;
    maxAttributeCount = count;
  }


  /**
   * Returns the flag that is used on the extended dn control.
   *
   * @return  extended dn control flag
   */
  public ExtendedDnControl.Flag getExtendedDnFlag()
  {
    return extendedDnFlag;
  }


  /**
   * Sets the flag to use on the extended dn control.
   *
   * @param  flag  to set on the extended dn control
   */
  public void setExtendedDnFlag(final ExtendedDnControl.Flag flag)
  {
    extendedDnFlag = flag;
  }


  /**
   * Performs a search operation with the {@link DirSyncControl}. The supplied
   * request is modified in the following way:
   *
   * <ul>
   *   <li>{@link SearchRequest#setControls(
   *     org.ldaptive.control.RequestControl...)} is invoked with {@link
   *     DirSyncControl}, {@link ShowDeletedControl}, and {@link
   *     ExtendedDnControl}</li>
   * </ul>
   *
   * @param  request  search request to execute
   *
   * @return  search operation response
   *
   * @throws  LdapException  if the search fails
   */
  public Response<SearchResult> execute(final SearchRequest request)
    throws LdapException
  {
    return execute(request, new DefaultCookieManager());
  }


  /**
   * Performs a search operation with the {@link DirSyncControl}. The supplied
   * request is modified in the following way:
   *
   * <ul>
   *   <li>{@link SearchRequest#setControls(
   *     org.ldaptive.control.RequestControl...)} is invoked with {@link
   *     DirSyncControl}, {@link ShowDeletedControl}, and {@link
   *     ExtendedDnControl}</li>
   * </ul>
   *
   * <p>The cookie is extracted from the supplied response and replayed in the
   * request.</p>
   *
   * @param  request  search request to execute
   * @param  response  of a previous dir sync operation
   *
   * @return  search operation response
   *
   * @throws  IllegalArgumentException  if the response does not contain a dir
   * sync cookie
   * @throws  LdapException  if the search fails
   */
  public Response<SearchResult> execute(
    final SearchRequest request,
    final Response<SearchResult> response)
    throws LdapException
  {
    final byte[] cookie = getDirSyncCookie(response);
    if (cookie == null) {
      throw new IllegalArgumentException(
        "Response does not contain a dir sync cookie");
    }

    return execute(request, new DefaultCookieManager(cookie));
  }


  /**
   * Performs a search operation with the {@link DirSyncControl}. The supplied
   * request is modified in the following way:
   *
   * <ul>
   *   <li>{@link SearchRequest#setControls(
   *     org.ldaptive.control.RequestControl...)} is invoked with {@link
   *     DirSyncControl}, {@link ShowDeletedControl}, and {@link
   *     ExtendedDnControl}</li>
   * </ul>
   *
   * <p>The cookie used in the request is read from the cookie manager and
   * written to the cookie manager after a successful search, if the response
   * contains a cookie.</p>
   *
   * @param  request  search request to execute
   * @param  manager  for reading and writing cookies
   *
   * @return  search operation response
   *
   * @throws  LdapException  if the search fails
   */
  public Response<SearchResult> execute(
    final SearchRequest request,
    final CookieManager manager)
    throws LdapException
  {
    final SearchOperation search = new SearchOperation(connection);
    request.setControls(createRequestControls(manager.readCookie()));

    final Response<SearchResult> response = search.execute(request);
    final byte[] cookie = getDirSyncCookie(response);
    if (cookie != null) {
      manager.writeCookie(cookie);
    }
    return response;
  }


  /**
   * Returns whether {@link #execute(SearchRequest, Response)} can be invoked
   * again.
   *
   * @param  response  of a previous paged results operation
   *
   * @return  whether more dir sync results can be retrieved from the server
   */
  public boolean hasMore(final Response<SearchResult> response)
  {
    return getDirSyncFlags(response) != 0;
  }


  /**
   * Invokes {@link #execute(SearchRequest, CookieManager)} with a {@link
   * DefaultCookieManager}.
   *
   * @param  request  search request to execute
   *
   * @return  search operation response of the last dir sync operation
   *
   * @throws  LdapException  if the search fails
   */
  public Response<SearchResult> executeToCompletion(final SearchRequest request)
    throws LdapException
  {
    return executeToCompletion(request, new DefaultCookieManager());
  }


  /**
   * Performs a search operation with the {@link DirSyncControl}. The supplied
   * request is modified in the following way:
   *
   * <ul>
   *   <li>{@link SearchRequest#setControls(
   *     org.ldaptive.control.RequestControl...)} is invoked with {@link
   *     DirSyncControl}, {@link ShowDeletedControl}, and {@link
   *     ExtendedDnControl}</li>
   * </ul>
   *
   * <p>This method will continue to execute search operations until all dir
   * sync search results have been retrieved from the server. The returned
   * response contains the response data of the last dir sync operation plus the
   * entries and references returned by all previous search operations.</p>
   *
   * <p>The cookie used for each request is read from the cookie manager and
   * written to the cookie manager after a successful search, if the response
   * contains a cookie.</p>
   *
   * @param  request  search request to execute
   * @param  manager  for reading and writing cookies
   *
   * @return  search operation response of the last dir sync operation
   *
   * @throws  LdapException  if the search fails
   */
  public Response<SearchResult> executeToCompletion(
    final SearchRequest request,
    final CookieManager manager)
    throws LdapException
  {
    Response<SearchResult> response = null;
    final SearchResult result = new SearchResult();
    final SearchOperation search = new SearchOperation(connection);
    byte[] cookie = manager.readCookie();
    long flags;
    do {
      if (response != null && response.getResult() != null) {
        result.addEntries(response.getResult().getEntries());
        result.addReferences(response.getResult().getReferences());
      }
      request.setControls(createRequestControls(cookie));
      response = search.execute(request);
      flags = getDirSyncFlags(response);
      cookie = getDirSyncCookie(response);
      if (cookie != null) {
        manager.writeCookie(cookie);
      }
    } while (flags != 0);
    response.getResult().addEntries(result.getEntries());
    response.getResult().addReferences(result.getReferences());
    return response;
  }


  /**
   * Returns the dir sync flags in the supplied response or -1 if no flags
   * exists.
   *
   * @param  response  of a previous dir sync operation
   *
   * @return  dir sync flags or -1
   */
  protected long getDirSyncFlags(final Response<SearchResult> response)
  {
    long flags = -1;
    final DirSyncControl ctl = (DirSyncControl) response.getControl(
      DirSyncControl.OID);
    if (ctl != null) {
      flags = ctl.getFlags();
    }
    return flags;
  }


  /**
   * Returns the dir sync cookie in the supplied response or null if no cookie
   * exists.
   *
   * @param  response  of a previous dir sync operation
   *
   * @return  dir sync cookie or null
   */
  protected byte[] getDirSyncCookie(final Response<SearchResult> response)
  {
    byte[] cookie = null;
    final DirSyncControl ctl = (DirSyncControl) response.getControl(
      DirSyncControl.OID);
    if (ctl != null) {
      if (ctl.getCookie() != null && ctl.getCookie().length > 0) {
        cookie = ctl.getCookie();
      }
    }
    return cookie;
  }


  /**
   * Returns the list of request controls configured for this client.
   *
   * @param  cookie  to add to the dir sync control or null
   *
   * @return  search request controls
   */
  private RequestControl[] createRequestControls(final byte[] cookie)
  {
    return
      new RequestControl[] {
        new DirSyncControl(dirSyncFlags, cookie, maxAttributeCount, true),
        new ExtendedDnControl(extendedDnFlag),
        new ShowDeletedControl(),
      };
  }
}
