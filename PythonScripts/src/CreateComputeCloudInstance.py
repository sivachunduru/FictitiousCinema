from OracleComputeCloud import OracleComputeCloud
import pprint
import json
import time
import sys


# a simple utility functions
def writeHostFile(ip):
    with open('hosts', 'wt') as hostsFile:
        hostsFile.write(ip)
        
def addResourceName(resourceNameDict, resourceType, resourceName):
    if resourceType in resourceNameDict:
        resourceNameDict[resourceType].append(resourceName)
    else:
        resourceNameDict[resourceType] = [resourceName]

# the main body

pprint.PrettyPrinter(indent=3)

if len(sys.argv) > 1:
    print(" args: " + str(sys.argv))
    username = sys.argv[1]
    password = sys.argv[2]
    authDomain = sys.argv[3]
    url = sys.argv[4]
else:
    username = 'testuser@oracle.com'
    password = 'password'

resourceNamePrefix = 'cloudnative'

occ = OracleComputeCloud(endPointUrl=url, authenticationDomain=authDomain)
occ.login(user=username, password=password)

#occ.debug = True

instance = None
ipReservation = None
# ipAssociation = None
# storage = None
# storageAttachment = None
# securityApplication = None
# securityIPList = None
securityList = None
# securityAssociation = None
securityRule = None

resourceNames = dict()

# add an SSH public key
#   does key already exist?
resourceTypeName = 'SSHKey'
simpleResourceName = occ.buildResourceName(resourceNamePrefix+'.pub')
fullResourceName = None
SSHKey = occ.getSSHKeys(sshKeyName=simpleResourceName)

if len(SSHKey) == 0:
    with open('PythonScripts/src/'+resourceNamePrefix+'.pub', 'rt') as keyFile:
        keyValue = keyFile.readline()

    SSHKeyMsgBody = { 'name': simpleResourceName,
                      'key': keyValue,
                      'enabled': True
                    }
    SSHKey = occ.createResource(resourceTypeName, SSHKeyMsgBody)
    fullResourceName = SSHKey['name']
    print('\nAdded ' + resourceTypeName + ', name = ' + fullResourceName)
else:
    fullResourceName = SSHKey['name']
    print('\nAlready exists: ' + resourceTypeName + ', name = ' + fullResourceName)

addResourceName(resourceNames, resourceTypeName, fullResourceName)

# create a storage volume
#   does the storage volume exist?
resourceTypeName = 'storageVolume'
simpleResourceName = occ.buildResourceName(resourceNamePrefix+'-storageVolume')
fullResourceName = None
storageVolume = occ.getStorageVolumes(storageVolumeName=simpleResourceName)

if len(storageVolume) == 0:
    storageVolumeMsgBody = { 'name': simpleResourceName,
                             'size': '22G',
                             'properties': ['/oracle/public/storage/default'],
                             'bootable': True,
                             'imagelist': '/oracle/public/OL_6.7_UEKR4_x86_64'
                           }
    storageVolume = occ.createResource(resourceTypeName, storageVolumeMsgBody)
    fullResourceName = storageVolume['name']
    print('\nCreated ' + resourceTypeName + ', name = ' + fullResourceName)
else:
    fullResourceName = storageVolume['name']
    print('\nAlready exists: ' + resourceTypeName + ', name = ' + fullResourceName)

addResourceName(resourceNames, resourceTypeName, fullResourceName)

# wait for storage volume to come online
while storageVolume['status'] != 'Online':
    print('Storage volume is not online. Wait for 30 seconds before re-check.')
    time.sleep(30)
    storageVolume = occ.getStorageVolumes(storageVolumeName=storageVolume['name'])
print('StorageVolume is online. Moving on.')

# create an ip reservation for a public ip
#   does the ip reservation exist?
resourceTypeName = 'ipReservation'
simpleResourceName = occ.buildResourceName(resourceNamePrefix+'-IPReservation')
fullResourceName = None
ipReservation = occ.getIPReservations(ipReservationName=simpleResourceName)

if len(ipReservation) == 0:
    ipReservationMsgBody = { 'parentpool': '/oracle/public/ippool',
                             'account': '/Compute-' + authDomain + '/default',
                             'permanent': True,
                             'name': simpleResourceName
                           }
    ipReservation = occ.createResource(resourceTypeName, ipReservationMsgBody)
    fullResourceName = ipReservation['name']
    print('\nCreated ' + resourceTypeName + ', name = ' + fullResourceName)
else:
    fullResourceName = ipReservation['name']
    print('\nAlready exists: ' + resourceTypeName + ', name = ' + fullResourceName)

addResourceName(resourceNames, resourceTypeName, fullResourceName)

# create a security list that allows inbound traffic but denies outbound traffic
resourceTypeName = 'securityList'
simpleResourceName = occ.buildResourceName(resourceNamePrefix+'-SecurityList')
fullResourceName = None
securityList = occ.getSecurityLists(securityListName=simpleResourceName)

if len(securityList) == 0:
    securityListMsgBody = { 'policy': 'permit',
                            'outbound_cidr_policy': 'permit',
                            'name': simpleResourceName
                          }
    securityList = occ.createResource(resourceTypeName, securityListMsgBody)
    fullResourceName = securityList['name']
    print('\nCreated ' + resourceTypeName + ', name = ' + fullResourceName)
else:
    fullResourceName = securityList['name']
    print('\nAlready exists: ' + resourceTypeName + ', name = ' + fullResourceName)

addResourceName(resourceNames, resourceTypeName, fullResourceName)

# create launchplan (instance)
resourceTypeName = 'launchPlan'
simpleResourceName = occ.buildResourceName(resourceNamePrefix+'-instance')
fullResourceName = None
# instance = occ.getInstances(instanceName='/Compute-gse00000588/cloud.admin/cloudnative-instance', user=username)

#if len(instance) == 0:
launchPlanMsgBody = { 'instances': [ { 'shape': 'oc3',
                                       'name': simpleResourceName,
                                       'sshkeys': [SSHKey['name']],
                                       'hostname': '',
                                       'reverse_dns': True,
                                       'label': 'This is a '+resourceNamePrefix+' instance.',
                                       'networking': { 'eth0': { 'seclists': [securityList['name']],
                                                                 'nat': 'ipreservation:'+ipReservation['name']
                                                               }
                                                     },
                                        #either use 'imagelist' or 'storage_attachments' and 'boot_order' combination
                                        #'imagelist': '/oracle/public/OL-6.4-20GB-x11-RD',
                                        'storage_attachments': [ { 'index': 1, 'volume': storageVolume['name']}],
                                        'boot_order': [1]
                                     }
                                   ]
                    }
instance = occ.createResource(resourceTypeName, launchPlanMsgBody)['instances'][0]
fullResourceName = instance['name']
resourceTypeName = 'instance'
print('\nCreated ' + resourceTypeName + ', name = ' + fullResourceName)
'''
else:
    fullResourceName = instance['name']
    resourceTypeName = 'instance'
    print('\nAlready exists: ' + resourceTypeName + ', name = ' + fullResourceName)
'''
addResourceName(resourceNames, resourceTypeName, fullResourceName)


# wait for a vcable_id
# if you do not specify 'networking' attribute in launch plan,
# you can create an ip association here.
# but you have to wait for the instance to startup and produce a vcable_id
# before you can create an ip association.
'''
while instance['vcable_id'] == None:
    print('No vcable_id yet. waiting for 60 seconds before retry')
    time.sleep(60)
    instance = occ.getInstances(instance['name'])
    #pp.pprint(instance)

print('Got vcable_id. Moving on.')
'''

# create an ip association to bind our instance to the ip reservation
# so that our instance has a public ip.
# this step can be skipped if a storageVolume is specified in the launchPlan
'''
resourceTypeName = 'ipAssociation'
ipAssociationMsgBody = { 'parentpool': 'ipreservation:'+ipReservation['name'],
                         'vcable': instance['vcable_id']
                       }
ipAssociation = occ.createResource(resourceTypeName, ipAssociationMsgBody)
resourceName = ipAssociation['name']
print('\nCreated ' + resourceTypeName + ', public ip: ' + ipAssociation['ip'] + ', name = ' + resourceName)
addResourceName(resourceNames, resourceTypeName, resourceName)
'''

## make SSH port 22 accessible from the internet

# create a security association to bind the instance (vcable_id) to the security list just created
# this step can be skipped if a securityList is specified in the launchPlan
'''
resourceTypeName = 'securityAssociation'
securityAssociationMsgBody = { 'vcable': instance['vcable_id'],
                               'seclist': securityList['name'],
                             }
securityAssociation = occ.createResource(resourceTypeName, securityAssociationMsgBody)
resourceName = securityAssociation['name']
print('\nCreated ' + resourceTypeName + ', name = ' + resourceName)
addResourceName(resourceNames, resourceTypeName, resourceName)
'''

# create a security rule to allow ssh application access to
resourceTypeName = 'securityRule'
simpleResourceName = occ.buildResourceName(resourceNamePrefix+'-SSHRule')
fullResourceName = None
securityRule = occ.getSecurityRules(securityRuleName=simpleResourceName)

if len(securityRule) == 0:
    securityRuleMsgBody = { 'name': simpleResourceName,
                            'application': '/oracle/public/ssh',
                            'src_list': 'seciplist:/oracle/public/public-internet',
                            'dst_list': 'seclist:' + securityList['name'],
                            'action': 'PERMIT',
                            'description': 'Allow SSH access to all servers'
                          }
    securityRule = occ.createResource(resourceTypeName, securityRuleMsgBody)
    fullResourceName = securityRule['name']
    print('\nCreated ' + resourceTypeName + ', name = ' + fullResourceName)
else:
    fullResourceName = securityRule['name']
    print('\nAlready exists: ' + resourceTypeName + ', name = ' + fullResourceName)

addResourceName(resourceNames, resourceTypeName, fullResourceName)

# make other ports (3306-3309) accessible from internet - Docker containers with MySQL use these ports
# first, create a Security Application for ports 3306-3309
resourceTypeName = 'securityApplication'
simpleResourceName = occ.buildResourceName(resourceNamePrefix + '-MySQL-securityApplication')
fullResourceName = None
securityApplication = occ.getSecurityApplications(securityApplicationName=simpleResourceName)

if len(securityApplication) == 0:
    securityApplicationMsgBody = { 'protocol': 'tcp',
                                   'name': simpleResourceName,
                                   'dport': '3306-3309'
                                 }
    securityApplication = occ.createResource(resourceTypeName, securityApplicationMsgBody)
    fullResourceName = securityApplication['name']
    print('\nCreated ' + resourceTypeName + ', name = ' + fullResourceName)
else:
    fullResourceName = securityApplication['name']
    print('\nAlready exists: ' + resourceTypeName + ', name = ' + fullResourceName)

addResourceName(resourceNames, resourceTypeName, fullResourceName)

# create a security rule to bind security application just created to the instance
resourceTypeName = 'securityRule'
simpleResourceName = occ.buildResourceName(resourceNamePrefix+'-MySQLRule')
fullResourceName = None
securityRule = occ.getSecurityRules(securityRuleName=simpleResourceName)

if len(securityRule) == 0:
    securityRuleMsgBody = { 'name': simpleResourceName,
                            'application': securityApplication['name'],
                            'src_list': 'seciplist:/oracle/public/public-internet',
                            'dst_list': 'seclist:' + securityList['name'],
                            'action': 'PERMIT',
                            'description': 'Allow access to ports 3306-3309 on all servers in the security list'
                          }
    securityRule = occ.createResource(resourceTypeName, securityRuleMsgBody)
    fullResourceName = securityRule['name']
    print('\nCreated ' + resourceTypeName + ', name = ' + fullResourceName)
else:
    fullResourceName = securityRule['name']
    print('\nAlready exists: ' + resourceTypeName + ', name = ' + fullResourceName)

addResourceName(resourceNames, resourceTypeName, fullResourceName)



with open(resourceNamePrefix + '-ResourceNames.json', 'wt') as nameFile:
    json.dump(resourceNames, nameFile, sort_keys=True, indent=3)

while instance['state'] != 'running':
    print('Instance is not up and running. Wait for 30 seconds before re-check.')
    time.sleep(30)
    instance = occ.getInstances(instanceName=instance['name'])
print('Instance is running. Moving on.')

writeHostFile(ipReservation['ip'])

print('Done creating Compute instance!')
