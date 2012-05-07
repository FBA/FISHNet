
package org.freshwaterlife.servlet.filters.sso;

// Fedora Libraries
import org.fcrepo.common.Constants;
import org.fcrepo.server.security.servletfilters.BaseCaching;
import org.fcrepo.server.security.servletfilters.ExtendedHttpServletRequest;

// Apache Commons Libraries
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Liferay Client Libraries
import com.liferay.client.soap.portal.model.UserSoap;
import com.liferay.client.soap.portal.model.RoleSoap;
import com.liferay.client.soap.portal.service.http.RoleServiceSoap;
import com.liferay.client.soap.portal.service.http.RoleServiceSoapService;
import com.liferay.client.soap.portal.service.http.RoleServiceSoapServiceLocator;
import com.liferay.client.soap.portal.service.http.UserServiceSoap;
import com.liferay.client.soap.portal.service.http.UserServiceSoapService;
import com.liferay.client.soap.portal.service.http.UserServiceSoapServiceLocator;

// Java Libraries
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

// OpenSSO Utility Library
import org.freshwaterlife.servlet.filters.sso.OpenSSOUtil;

/**
 * @author Eric Liao
 */
public class OpenSSOFilter extends BaseCaching implements Constants{
	
	private static final String PORTAL_ADMIN = "";                                               
	private static final String PORTAL_ADMIN_PASSWORD = "";                                   
	private static final String LIFERAY_URL = "";                
	private static final String LIFERAY_PORT = "";                                                
	private static final String SERVICE_URL = "";
	
	private static final String _SUBJECT_ID_KEY = "open.sso.subject.id";
	
	protected static Log logger = LogFactory.getLog(OpenSSOFilter.class);
	@SuppressWarnings("rawtypes")
	private Map<String, Set> namedAttributes = null;
	private HashSet<String> attributeValues = null;
	
	@Override
    public void destroy() {
        String method = "destroy()";
        if (logger.isDebugEnabled()) {
        	logger.debug(enter(method));
        }
        super.destroy();
        if (logger.isDebugEnabled()) {
        	logger.debug(exit(method));
        }
    }
	
	@Override
    protected void initThisSubclass(String key, String value) {
         super.initThisSubclass(key,value);
        String method = "initThisSubclass()";
        if (logger.isDebugEnabled()) {
        	logger.debug(enter(method));
        }              
    }
	
	@SuppressWarnings("rawtypes")
	@Override
    public void authenticate(ExtendedHttpServletRequest extendedHttpServletRequest)
            throws Exception {
        
		RoleSoap[] lrUserRoles;
		String[] roles = null;
		RoleSoap[] allRoles = null;
		long lDefaultUserId;
		String companyid = "1";						
		ArrayIterator it;
		RoleSoap role;
		Boolean ssoAuthenticated = false;
		
		namedAttributes = new Hashtable<String, Set>();
		attributeValues = new HashSet<String>();
		
		String method = "authenticate()";
        if (logger.isDebugEnabled()) {
            logger.debug(enter(method));
        }                         
        
        try {
        	
        	String userid = "opensso";
            if (logger.isDebugEnabled()) {
                logger.debug(format(method, null, "userid", userid));
            }
        	                      
            ssoAuthenticated = OpenSSOUtil.isAuthenticated(
					extendedHttpServletRequest, SERVICE_URL);
			
			if (ssoAuthenticated) {

				if (logger.isDebugEnabled()) {
                    logger.debug(format(method, "authenticated to OpenSSO"));
                }
				
				String newSubjectId = OpenSSOUtil.getSubjectId(
						extendedHttpServletRequest, SERVICE_URL);
					
				HttpSession session = extendedHttpServletRequest.getSession();

				String oldSubjectId = (String)session.getAttribute(
					_SUBJECT_ID_KEY);

				if (oldSubjectId == null) {
					session.setAttribute(_SUBJECT_ID_KEY, newSubjectId);
				}
				else if (!newSubjectId.equals(oldSubjectId)) {
					session.invalidate();
					session = extendedHttpServletRequest.getSession();
					session.setAttribute(_SUBJECT_ID_KEY, newSubjectId);
				}
				
				if (logger.isDebugEnabled()) {
                    logger.debug(format(method, "Subject id: " + newSubjectId));
                }												
				
				Map<String, String> nameValues = OpenSSOUtil.getAttributes(
						extendedHttpServletRequest, SERVICE_URL);						
				String emailAddress = nameValues.get("mail");								
												
				// Use LR web-services to query user roles			
				long companyId = Long.parseLong(companyid);
								
				UserServiceSoapService usersvc = new UserServiceSoapServiceLocator();						
				UserServiceSoap authsoap = null;					
		        
				try {					
					authsoap = usersvc.getPortal_UserService(
							_getURL(PORTAL_ADMIN, PORTAL_ADMIN_PASSWORD, "Portal_UserService"));
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
		        
				UserSoap user = null;
				
		        try {
		            user = authsoap.getUserByEmailAddress(companyId, emailAddress);
		        } catch (java.rmi.RemoteException re) {
		            System.out.println(re.toString());	            
		        }				        	        
		        
		        long LRuserid = user.getUserId();	        	        
		        
		        String[] userId = {"u" + Long.toString(LRuserid)};
		        
		        RoleServiceSoapService rolesvc = new RoleServiceSoapServiceLocator();						
				RoleServiceSoap rolesoap = null;
		        
				try {
					rolesoap = rolesvc.getPortal_RoleService(
							_getURL(PORTAL_ADMIN, PORTAL_ADMIN_PASSWORD, "Portal_RoleService"));
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				lrUserRoles = rolesoap.getUserRoles(LRuserid);						
				lDefaultUserId = authsoap.getDefaultUserId(companyId);
		        
				if (LRuserid != lDefaultUserId) {
					RoleSoap[] lrDefaultUserRoles;
					lrDefaultUserRoles = rolesoap.getUserRoles(lDefaultUserId);	
					allRoles = (RoleSoap[]) ArrayUtils.addAll(lrUserRoles, lrDefaultUserRoles);				
				}						
				
				if (ArrayUtils.isNotEmpty(allRoles)) {				
					it = new ArrayIterator(lrUserRoles);
					while (it.hasNext()) {
	                    role = (RoleSoap) it.next();
	                    String[] thisRole = {"r" + Long.toString(role.getRoleId())};
	                    roles = (String[]) ArrayUtils.addAll(roles, thisRole);                    
	                }				
				}															
													
				roles = (String[]) ArrayUtils.addAll(roles, userId);
								
				for (String thisRole : roles) {
					attributeValues.add(thisRole);
				}				
				namedAttributes.put("fedoraRole", attributeValues);				
			
				Principal authenticatingPrincipal =
		            new org.fcrepo.server.security.servletfilters.Principal(userid);
				extendedHttpServletRequest.setAuthenticated(authenticatingPrincipal,
		                              FILTER_NAME);
			    if (logger.isDebugEnabled()) {
			        logger.debug(format(method, "set authenticated"));
			    }
                
            }
							        	      	         		
			else {
				// authenticate if request comes from SAXON servlet
				Enumeration<?> names = extendedHttpServletRequest.getHeaderNames();
				if (names != null) {
		            while (names.hasMoreElements()) {
		            	String name = (String) names.nextElement();
		            	Enumeration<?> values = extendedHttpServletRequest.getHeaders(name); // support multiple values
		            	if (values != null) {
		            		while (values.hasMoreElements()) {
		            			String value = (String) values.nextElement();                  
		            			if (name.equals("saxon") && value.equals("saxon")) {		            				
		            				if (logger.isDebugEnabled()) {
				                		logger.debug(format(method, "request from SAXON"));
				                	}                		
		            				attributeValues.add("administrator");	  					
		            				namedAttributes.put("fedoraRole", attributeValues);
		                	  
		            				Principal authenticatingPrincipal =
				      		            new org.fcrepo.server.security.servletfilters.Principal(userid);
				      				extendedHttpServletRequest.setAuthenticated(authenticatingPrincipal,
				      		                              FILTER_NAME);
				      			    if (logger.isDebugEnabled()) {
				      			        logger.debug(format(method, "set authenticated"));
				      			    }
		            			}		                  
		            		}
		            	}
		            }					
				}
			}						
			
            if (logger.isDebugEnabled()) {
                logger.debug(format(method, "calling audit", "user", userid));
            }            
        } 
        catch (Throwable th) {
            logger.error("Error authenticating", th);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(exit(method));
        }                
    }
	
	@Override
	public void contributeAuthenticatedAttributes(
			ExtendedHttpServletRequest extendedHttpServletRequest)
			throws Exception {
		String method = "gatherAuthenticatedAttributes()";
		if (logger.isDebugEnabled()) {
			logger.debug(enter(method));
		}				
		
		extendedHttpServletRequest.addAttributes("fedoraRole", namedAttributes);
		
		if (logger.isDebugEnabled()) {
			logger.debug(exit(method));
		}
	}
	
	private URL _getURL(String remoteUser, String password, String serviceName) throws Exception {

		String url = "http://" + remoteUser + ":" + password + 
			  "@" + LIFERAY_URL + ":" + LIFERAY_PORT + "/tunnel-web/secure/axis/" + serviceName;		
		return new URL(url);
	}
}