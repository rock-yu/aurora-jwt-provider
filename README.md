# High Level Summary

Given a multi-tenant SaaS application used as a Collaboration platform (e.g: Jira), data is siloed into _Projects_, with access inside a Project controlled by each _Organization_. A Delegate Admin methodology is used to allow Organizations to maintain their own access control within their data partition as required.

This project proposed a simple JWT model with common user attributes which can be calculated at Login time and injected on every request. Hence, sub-systems can use the attributes encoded in the JWT to perform common tasks such as preference based rendering or authorization without having to rely on Trusted API call-back to central authorization endpoint to look up this information 

A client library is provided to encode/decode the proposed JWT token  

# Core Concepts

3 core concepts to segment access:

* **User** - An individual person wishing to access the platform
* **Organization** - A User belongs to a single Organization. Data is _owned_ by an Organization
* **Project** - A container for all data relating to a single unit of work, such as a new Metro tunnel project, but _partitioned_ by Organization

# JWT Structure

The structure of the JWT should have 4 main components:

* **Version identifier** - to allow backwards & forward compatibility, the payload of the JWT should be versioned.
* **Identity** - identifies the user making the request and forms the Authentication part
* **Preferences** - key preference info, common Preferences that are pervasively used, but rarely change can be included in the payload
* **Authorization** - Outlines a combination of which Projects the user is a member of, and which Organization-wide & Project-specific Secured Assets the User has.  To minimise the size of the Payload, the values of these 2 sections are encoded as a bitmask.

## Sample JWT payload 

    {
        "version": 1,               
        "identity": {                
            "userId": "223355",        
            "organizationId": "969"
        },
        "preferences": {
            "locale": "en_au",
            "timezone": "Australia/Melbourne",
            "fileEncoding": "utf-8"
        },
        "authorization": {
            "organization": "s",   // Base-36 encoded form of the number that represents the Bitmask 
            "projects": {
                "26905": "CJZ23",  // Base-36 encoded form of the number that represents the Bitmask 
                "28318": "230A"         
            }
        },
        "exp": "1485415857"
    }
