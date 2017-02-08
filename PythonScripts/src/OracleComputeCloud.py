import requests
import requests.packages.urllib3
# from requests_toolbelt.utils import dump
import urllib

class OracleComputeCloud():
    '''
    this class encapsulates operations that can be performed on Oracle Compute Cloud service 
    '''

    # get operations
    
    def __init__(self, endPointUrl, authenticationDomain, cookies=None):
        '''
        Must provide an end point URL and an authentication domain
        '''
        
        self.__endPointUrl = endPointUrl
        self.__authenticationDomain = authenticationDomain
        self.__cookies = cookies
        self.__contentType = 'application/oracle-compute-v3+json'
        self.__accept = 'application/oracle-compute-v3+json'
        self.debug = False
        requests.packages.urllib3.disable_warnings()
        
        # define all resource paths
        self.resourcePaths = { 'account': '/account', 
                               'imageList': '/imagelist',
                               'instance': '/instance',
                               'ipAssociation': '/ip/association',
                               'ipReservation': '/ip/reservation',
                               'launchPlan': '/launchplan',
                               'machineImage': '/machineimage',
                               'orchestration': '/orchestration',
                               'securityApplication': '/secapplication',
                               'securityAssociation': '/secassociation',
                               'securityIPList': '/seciplist',
                               'securityList': '/seclist',
                               'securityRule': '/secrule',
                               'SSHKey': '/sshkey',
                               'storageAttachment': '/storage/attachment',
                               'storageProperty': '/property/storage',
                               'storageVolume': '/storage/volume',
                               'VPNEndpoint': '/vpnendpoint',
                               'shape': '/shape',
                             }
        
    def login(self, user, password):
        '''
        returns cookies if login is successful, None otherwise
        '''
        if (self.__cookies != None):
            return self.__cookies
        
        url = self.__endPointUrl + '/authenticate/'
        headerString = {'Content-Type':self.__contentType, 'Accept':self.__accept}
        fullUsername = '/Compute-' + self.__authenticationDomain + '/' + user
        authenticationString = {"password": password, "user": fullUsername}
        
        response = requests.post(url, json=authenticationString, headers=headerString)
        
        #data = dump.dump_all(response)
        #print(data.decode('utf-8'))

        if response.status_code == 204:
            self.__cookies = response.cookies
            self.__user = user
            return response.cookies
        
    def refresh(self, cookies=None):
        if (cookies != None):
            self.__cookies = cookies
            
        url = self.__endPointUrl + '/refresh/'
        headerString = {'Content-Type':self.__contentType, 'Accept':self.__accept}
        response = requests.get(url, headers=headerString, cookies=self.__cookies)
        
        #data = dump.dump_all(response)
        #print(data.decode('utf-8'))

        if response.status_code == 204:
            return self.__cookies
    
    def buildContainerUri(self, isPublic=None, user=None):
        container = '/Compute-' + self.__authenticationDomain
        if isPublic == True:
            return  '/oracle/public'
        if user == None:
            return container + '/' + self.__user
        else:
            return container + '/' + user 

    # utility methods
    def getResources(self, resourcePath, container, resourceName='ALL', queryParams=None):
        url = self.__endPointUrl + resourcePath

        if resourceName == 'ALL':
            url = url + container
            if not url.endswith('/'):
                url += '/'
            if queryParams != None and type(queryParams) is dict:
                url += '?' + urllib.urlencode(queryParams)
            if url.endswith('?'):
                url = url[:-1]
                
        elif resourceName.startswith('/'):
            url = url + resourceName
        else:
            url = url + '/' + resourceName
            
        headerString = {'Content-Type':self.__contentType, 'Accept':self.__accept}
        response = requests.get(url, headers=headerString, cookies=self.__cookies)
        
        self.debugLog(response)
        
        if response.status_code == 200:
            jsonResponse = response.json()
            return (jsonResponse['result'] if resourceName=='ALL' else jsonResponse)
        elif response.status_code == 404:
            return []
        else:
            raise OCCException('Response code: ' + str(response.status_code) + ', ' + str(response.content))
    def deleteResource(self, resourceTypeName, resourceName):
        url = self.__endPointUrl + self.resourcePaths[resourceTypeName]
        
        if resourceName.startswith('/'):
            url = url + resourceName
        else:
            url = url + '/' + resourceName

        headerString = {'Content-Type':self.__contentType, 'Accept':self.__accept}
        response = requests.delete(url, headers=headerString, cookies=self.__cookies)
        self.debugLog(response)
        
        if response.status_code == 204:
            return response.status_code
        else:
            raise OCCException('Response code: ' + str(response.status_code) + ', ' + str(response.content))
    def createResource(self, resourceTypeName, messageBody):
        url = self.__endPointUrl + self.resourcePaths[resourceTypeName] + '/'
        headerString = {'Content-Type':self.__contentType, 'Accept':self.__accept}
        response = requests.post(url=url, headers=headerString, cookies=self.__cookies, json=messageBody)
        self.debugLog(response)
        
        if response.status_code == 201:
            return (response.json())
        else:
            raise OCCException('Response code: ' + str(response.status_code) + ', ' + str(response.content))
     
    def getAccounts(self, accountName='ALL'):
        resourcePath = self.resourcePaths['account']
        container = '/Compute-' + self.__authenticationDomain
        return self.getResources(resourcePath, container, accountName)

    def getImageLists(self, imageListName='ALL', isPublic=True, user=None):
        '''
        if imageListName == 'ALL'
            returns a list of dictionaries of image list details
        else
            returns a dictionary

        '''
        resourcePath = self.resourcePaths['imageList'] 
        container = self.buildContainerUri(isPublic, user)
        return self.getResources(resourcePath, container, imageListName)

    def getInstances(self, instanceName='ALL', user=None):
        '''
        if instanceName == 'ALL'
            returns a list of dictionaries of instance details
        else
            returns a dictionary
        '''
        resourcePath = self.resourcePaths['instance']
        container = self.buildContainerUri(user=user)
        return self.getResources(resourcePath, container, instanceName)

    def getIPAssociations(self, ipAssociationName='ALL', user=None, queryParams=None):    
        resourcePath = self.resourcePaths['ipAssociation']
        container = self.buildContainerUri(user=user)
        return self.getResources(resourcePath, container, ipAssociationName, queryParams)
    
    def getIPReservations(self, ipReservationName='ALL', user=None, queryParams=None):
        resourcePath = self.resourcePaths['ipReservation']
        container = self.buildContainerUri(user=user)
        return self.getResources(resourcePath, container, ipReservationName, queryParams)

    def getMachineImages(self, machineImageName='ALL', isPublic=True, user=None):
        resourcePath = self.resourcePaths['machineImage']
        container = self.buildContainerUri(isPublic, user)
        return self.getResources(resourcePath, container, machineImageName)

    def getOrchestrations(self, orchestrationName='ALL', user=None, status=None):            
        resourcePath = self.resourcePaths['orchestration']
        container = self.buildContainerUri(user=user)
        queryParams = {}
        if status != None:
            queryParams = {"status":status}
        
        return self.getResources(resourcePath, container, orchestrationName, queryParams)

    def getSecurityApplications(self, securityApplicationName='ALL', isPublic=True, user=None, queryParams=None):            
        resourcePath = self.resourcePaths['securityApplication']
        container = self.buildContainerUri(isPublic, user)
        return self.getResources(resourcePath, container, securityApplicationName, queryParams)
        
    def getSecurityAssociations(self, securityAssociationName='ALL', user=None, queryParams=None):            
        resourcePath = self.resourcePaths['securityAssociation']
        container = self.buildContainerUri(user=user)
        return self.getResources(resourcePath, container, securityAssociationName, queryParams)

    def getSecurityIPLists(self, securityIPListName='ALL', isPublic=True, user=None):            
        resourcePath = self.resourcePaths['securityIPList']
        container = self.buildContainerUri(isPublic, user)
        return self.getResources(resourcePath, container, securityIPListName)

    def getSecurityLists(self, securityListName='ALL', user=None):            
        resourcePath = self.resourcePaths['securityList']
        container = self.buildContainerUri(user)
        return self.getResources(resourcePath, container, securityListName)

    def getSecurityRules(self, securityRuleName='ALL', user=None, queryParams=None):            
        resourcePath = self.resourcePaths['securityRule']
        container = self.buildContainerUri(user)
        return self.getResources(resourcePath, container, securityRuleName, queryParams)

    def getShapes(self, shapeName='ALL'):
        '''
        if shapeName == None:
            return a list of dictionaries of all shapes, each of which contains a set of shape information
        else
            return a dictionaries of the named shape
            
        sample returns
        [{"nds_iops_limit": 0, "ram": 7680, "cpus": 2.0, "root_disk_size": 0, "uri": "https://api-z24.compute.us6.oraclecloud.com/shape/oc3", "io": 200, "name": "oc3"}, 
         {"nds_iops_limit": 0, "ram": 15360, "cpus": 2.0, "root_disk_size": 0, "uri": "https://api-z24.compute.us6.oraclecloud.com/shape/oc1m", "io": 200, "name": "oc1m"}]
        '''
        
        resourcePath = self.resourcePaths['shape']
        container = ''
        return self.getResources(resourcePath, container, shapeName)
    
    def getSSHKeys(self, sshKeyName='ALL', user=None):            
        resourcePath = self.resourcePaths['SSHKey']
        container = self.buildContainerUri(user)
        return self.getResources(resourcePath, container, sshKeyName)
    
    def getStorageAttachments(self, storageAttachmentName='ALL', user=None, queryParams=None):
        resourcePath = self.resourcePaths['storageAttachment']
        container = self.buildContainerUri(user)
        return self.getResources(resourcePath, container, storageAttachmentName, queryParams)

    def getStorageProperties(self, storagePropertyName='ALL'):
        resourcePath = self.resourcePaths['storageProperty']
        container = self.buildContainerUri(isPublic=True)
        return self.getResources(resourcePath, container, storagePropertyName)

    def getStorageVolumes(self, storageVolumeName='ALL', user=None):
        resourcePath = self.resourcePaths['storageVolume']
        container = self.buildContainerUri(user)
        return self.getResources(resourcePath, container, storageVolumeName)

    def getVPNEndpoints(self, vpnEndpointName='ALL'):
        resourcePath = self.resourcePaths['VPNEndpoint']
        container = container = '/Compute-' + self.__authenticationDomain
        return self.getResources(resourcePath, container, vpnEndpointName)

    '''
    utility operations
    '''
    def buildResourceName(self, simpleName):
        if not simpleName.startswith('/'):
            return "/Compute-" + self.__authenticationDomain + '/' + self.__user + '/' + simpleName
        else:
            return  "/Compute-" + self.__authenticationDomain + '/' + self.__user + simpleName

    def debugLog(self, response):
        # debug
        if self.debug:
        #    data = dump.dump_all(response)
            print(data.decode('utf-8'))


class OCCException(Exception):
    pass
