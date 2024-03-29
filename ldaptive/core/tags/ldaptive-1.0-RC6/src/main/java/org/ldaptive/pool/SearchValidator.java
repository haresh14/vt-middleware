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
package org.ldaptive.pool;

import org.ldaptive.Connection;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates a connection is healthy by performing a search operation.
 * Validation is considered successful if the search result size is greater than
 * zero.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchValidator implements Validator<Connection>
{

  /** Logger for this class. */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /** Search request to perform validation with. */
  private SearchRequest searchRequest;


  /** Creates a new search validator. */
  public SearchValidator()
  {
    searchRequest = new SearchRequest();
    searchRequest.setBaseDn("");
    searchRequest.setSearchFilter(new SearchFilter("(objectClass=*)"));
    searchRequest.setReturnAttributes(new String[0]);
    searchRequest.setSearchScope(SearchScope.OBJECT);
    searchRequest.setSizeLimit(1);
  }


  /**
   * Creates a new search validator.
   *
   * @param  sr  to use for searches
   */
  public SearchValidator(final SearchRequest sr)
  {
    searchRequest = sr;
  }


  /**
   * Returns the search request.
   *
   * @return  search request
   */
  public SearchRequest getSearchRequest()
  {
    return searchRequest;
  }


  /**
   * Sets the search request.
   *
   * @param  sr  search request
   */
  public void setSearchRequest(final SearchRequest sr)
  {
    searchRequest = sr;
  }


  /** {@inheritDoc} */
  @Override
  public boolean validate(final Connection c)
  {
    boolean success = false;
    if (c != null) {
      try {
        final SearchOperation search = new SearchOperation(c);
        final SearchResult result = search.execute(searchRequest).getResult();
        success = result.size() > 0;
      } catch (Exception e) {
        logger.debug(
          "validation failed for search request {}",
          searchRequest,
          e);
      }
    }
    return success;
  }
}
