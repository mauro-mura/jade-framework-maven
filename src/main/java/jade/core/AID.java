package jade.core;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import jade.lang.acl.StringACLCodec;
import jade.util.leap.Properties;

/**
 * This class represents a JADE Agent Identifier. JADE internal agent
 * tables use this class to record agent names and addresses.
 * @author Moreno LAGO
 */
public class AID implements Comparable<AID>, Serializable {

	@Serial
	private static final long serialVersionUID = -5443350517037113743L;

	public static final char HAP_SEPARATOR = '@';

    // Unique ID of the platform, used to build the GUID of resident agents.
    private static String platformID;

    private String name;
    private int hashCode;

    private static final int EXPECTED_ADDRESSES_SIZE = 1;
    private static final int EXPECTED_RESOLVERS_SIZE = 1;

    private List<String> addresses = new ArrayList<>(EXPECTED_ADDRESSES_SIZE);
    private List<AID> resolvers = new ArrayList<>(EXPECTED_RESOLVERS_SIZE);

    private Properties userDefSlots = new Properties();

    /**
     * Constructs an Agent-Identifier whose slot name is set to an empty string
     */
    public AID() {
        this("", ISGUID);
    }

    /**
     * Constructor for an Agent-identifier
     * @param name is the value for the slot name for the agent.
     * @param isGUID indicates if the passed <code>name</code>
     * is already a globally unique identifier or not. Two
     * constants <code>ISGUID</code>, <code>ISLOCALNAME</code>
     * have also been defined for setting a value for this parameter.
     * If the name is a local name, then the HAP (Home Agent Platform)
     * is concatenated to the name, separated by  "@".
     **/
    public AID(String name, boolean isGUID) {
			if (isGUID) {
				setName(name);
			}
			else {
				setLocalName(name);
			}
    }

    public static String getPlatformID() {
        return platformID;
    }

    public static void setPlatformID(String id) {
        platformID = id;
    }

    /** constant to be used in the constructor of the AID **/
    public static final boolean ISGUID = true;
    
    /** constant to be used in the constructor of the AID **/
    public static final boolean ISLOCALNAME = false;

    /**
     * Key to retrieve the agent class name as a user defined slot of
     * the AID included in the AMSAgentDescription registered with
     * the AMS.
     */
    public static final String AGENT_CLASSNAME = "JADE-agent-classname";

    /**
     * Key to retrieve the name of the originator agent in case the agent represented by this AID was cloned
     */
    public static final String CLONE_ORIGINATOR = "JADE-Clone-Originator";

    /**
     * This method permits to set the symbolic name of an agent.
     * The passed parameter must be a GUID and not a local name.
     */
    public void setName(String n) {
        name = n.trim();
        hashCode = name.toLowerCase().hashCode();
    }

    /**
     * This method permits to set the symbolic name of an agent.
     * The passed parameter must be a local name.
     */
    public void setLocalName(String n) {
        String hap = getPlatformID();
        if (hap == null) {
            throw new RuntimeException("Unknown Platform Name");
        }
        name = n.trim();
        name = createGUID(name, hap);
        hashCode = name.toLowerCase().hashCode();
    }

    public static String createGUID(String localName, String platformName) {
        String n = localName.trim();
        return n.concat(HAP_SEPARATOR + platformName);
    }

    /**
     * This method returns the name of the agent.
     */
    public String getName() {
        return name;
    }

    /**
     * This method permits to add a transport address where
     * the agent can be contacted.
     * The address is added only if not yet present
     */
    public void addAddresses(String url) {
        if (!addresses.contains(url)) {
            addresses.add(url);
        }
    }

    /**
     * To remove a transport address.
     * @param url the address to remove
     * @return true if the address has been found and removed, false otherwise.
     */
    public boolean removeAddresses(String url) {
        return addresses.remove(url);
    }

    /**
     * To remove all addresses of the agent
     */
    public void clearAllAddresses() {
        addresses.clear();
    }

    /**
     * Returns an iterator of all the addresses of the agent.
     * @see java.util.Iterator
     */
    public Iterator<String> getAllAddresses() {
        return addresses.iterator();
    }

    /**
     * This method permits to add the AID of a resolver (an agent where name
     * resolution services for the agent can be contacted)
     */
    public void addResolvers(AID aid) {
        if (!resolvers.contains(aid)) {
            resolvers.add(aid);
        }
    }

    /**
     * To remove a resolver.
     * @param aid the AID of the resolver to remove
     * @return true if the resolver has been found and removed, false otherwise.
     */
    public boolean removeResolvers(AID aid) {
        return resolvers.remove(aid);
    }

    /**
     * To remove all resolvers.
     */
    public void clearAllResolvers() {
        resolvers.clear();
    }

    /**
     * Returns an iterator of all the resolvers.
     * @see java.util.Iterator
     */
    public Iterator<AID> getAllResolvers() {
        return resolvers.iterator();
    }

    /**
     * To add a user defined slot (a pair key, value).
     * @param key the name of the property
     * @param value the corresponding value of the property
     */
    public void addUserDefinedSlot(String key, String value) {
        userDefSlots.setProperty(key, value);
    }

    /**
     * To remove a user defined slot.
     * @param key the name of the property
     * @return true if the property has been found and removed, false otherwise
     */
    public boolean removeUserDefinedSlot(String key) {
        return userDefSlots.remove(key) != null;
    }

    /**
     * Returns an array of string containing all the addresses of the agent
     */
    public String[] getAddressesArray() {
    	Object[] objs = addresses.toArray();
		String[] result = new String[objs.length];
		System.arraycopy(objs, 0, result, 0, objs.length);
		return result;
    }
    
    // For persistence service
    @SuppressWarnings("unused")
	private void setAddressesArray(String[] arr) {
		addresses.clear();
		for(int i = 0; i < arr.length; i++) {
			addAddresses(arr[i]);
		}
	}

    /**
     * Returns an array containing all the AIDs of the resolvers.
     */
    public AID[] getResolversArray() {
    	Object[] objs = resolvers.toArray();
		AID[] result = new AID[objs.length];
		System.arraycopy(objs, 0, result, 0, objs.length); 
		return result;
    }
    
    // For persistence service
 	@SuppressWarnings("unused")
	private void setResolversArray(AID[] arr) {
 		resolvers.clear();
 		for(int i = 0; i < arr.length; i++) {
 			addResolvers(arr[i]);
 		}
 	}

    /**
     * Returns the user-defined slots as properties.
     * @return all the user-defined slots as a <code>java.util.Properties</code> java Object.
     * @see java.util.Properties
     */
    public Properties getAllUserDefinedSlot() {
        return userDefSlots;
    }

    /**
     * Converts this agent identifier into a readable string.
     * @return the String full representation of this AID
     **/
    public String toString() {
    	StringBuilder s = new StringBuilder("( agent-identifier ");
		StringACLCodec.appendACLExpression(s, ":name", name);
			if (!addresses.isEmpty()) {
				s.append(" :addresses (sequence ");
			}
			for (int i = 0;i < addresses.size();i++) {
				try {
					s.append(addresses.get(i));
					s.append(" ");
				}
				catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			if (!addresses.isEmpty()) {
				s.append(")");
			}
			if (!resolvers.isEmpty()) {
				s.append(" :resolvers (sequence ");
			}
		for (int i=0; i<resolvers.size(); i++) { 
			try {
				s.append(resolvers.get(i).toString());
			} 
			catch (IndexOutOfBoundsException e) {e.printStackTrace();}
			s.append(" ");
		}
			if (!resolvers.isEmpty()) {
				s.append(")");
			}
		Enumeration<?> e = userDefSlots.propertyNames();
		String key;
		String value;
		while (e.hasMoreElements()) {
			key = (String) e.nextElement();
			value = userDefSlots.getProperty(key);
			s.append(" :X-");
			StringACLCodec.appendACLExpression(s, key, value); 
		}
		s.append(")");
		return s.toString();
    }

    /**
     * Clone the AID object.
     */
    public synchronized AID clone() {
        AID result = new AID(this.name, ISGUID);
        result.persistentID = null;
        result.addresses = new ArrayList<>(addresses);
		result.resolvers = new ArrayList<>(resolvers);
		result.userDefSlots = (Properties) userDefSlots.clone();
		return result;
    }

    /**
     * Equality operation. This method compares an <code>AID</code> object with
     * another or with a Java <code>String</code>. The comparison is case
     * insensitive.
     * @param o The Java object to compare this <code>AID</code> to.
     * @return <code>true</code> if one of the following holds:
     * <ul>
     * <li> The argument <code>o</code> is an <code>AID</code> object
     * with the same <em>GUID</em> in its name slot (apart from
     * differences in case).
     * <li> The argument <code>o</code> is a <code>String</code> that is
     * equal to the <em>GUID</em> contained in the name slot of this
     * Agent ID (apart from differences in case).
     * </ul>
     */  
    @Override
   	public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
//   		if (getClass() != obj.getClass())
//   			return false;
   		AID other = (AID) obj;
   		return Objects.equals(name.toLowerCase(), other.name.toLowerCase());
   	}

   	@Override
   	public int hashCode() {
   		return hashCode;
   	}

    /**
     * Comparison operation. This operation imposes a total order
     * relationship over Agent IDs.
     * @param o Another <code>AID</code> object, that will be compared
     * with the current <code>AID</code>.
     * @return -1, 0 or 1 according to the lexicographical order of the
     * <em>GUID</em> of the two agent IDs, apart from differences in
     * case.
     */
    @Override
    public int compareTo(AID o) {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }

    /**
     * Returns the local name of the agent (without the HAP).
     * If the agent is not local, then the method returns its GUID.
     */
    public String getLocalName() {
        int atPos = name.lastIndexOf(HAP_SEPARATOR);
			if (atPos == -1) {
				return name;
			}
			else {
				return name.substring(0, atPos);
			}
    }

    /**
     * Returns the HAP of the agent or null if the GUID of this
     * <code>AID</code> is not of the form <local-name>@<platform-name>
     */
    public String getHap() {
        int atPos = name.lastIndexOf(HAP_SEPARATOR);
			if (atPos == -1) {
				return null;
			}
			else {
				return name.substring(atPos + 1);
			}
    }
    
    // For persistence service
 	private transient Long persistentID;
 	
 	// For persistence service
 	@SuppressWarnings("unused")
	private Long getPersistentID() {
 		return persistentID;
 	}
 	
 	// For persistence service
 	@SuppressWarnings("unused")
	private void setPersistentID(Long l) {
 		persistentID = l;
 	}

}