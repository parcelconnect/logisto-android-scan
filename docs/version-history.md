VERSION HISTORY
---------------

**App Name**: Fastway Scan+Sort 
**Package Name**: ie.fastway.scansort
**Company**: Fastway Ireland

Authors:
    - Paulo Tubbert-Semiao
        - Initials: "PTS"
        - Company: Logistio.ie
        - Email (work): paulo@logistio.ie
        - Email (personal): paulotubbert@gmail.com / paulo@tubbert.com

----


v0.0.1
------
- BACKLOG:
    - Config screen for SQLite. Allow the user to clear the SQLite cache. 
    - Show history of scan events as small cards under the main card 
        in the Scanning Scene.
    - Show "Pathfinder 6140" as common name for AVD ConnectableDevices.
    - Detect trying to pair with same AVD scanner twice.
    - Record the last AVD paired with.
    - Print labels with AVD devices.
    - Add NavigationDrawer that allows user to unpair with a device.
    - Show an error in the NetworkInfo bar if the Auth fails.
    - Add StackDriver for logging.
    - Add functionality from AVD demo app. (change scanning mode, print test label, etc)
    - Change MainSceneCoordinator to ensure that the scene transitions are executed. 
    - Check if Bluetooth is turned on.
    - Repair connections with Avd Scanner.    
    - Show version code in sidebar.
    - Update IMEI code and sim serial numbers.
    
    - Show totalAvailable in ShipmentCatalog API in WaitingInfo bar.
    
    - TODO: Add "Reset to Factory Default" barcode in config screen for Symbol (2017-11-20)

    - TODO: Connect to AVD device on background thread.
    
    - TODO: Show Version info in NavigationDrawer
    
    - TODO: Add "Reset to Factory Default" barcode in config screen for Symbol (2017-11-20)
    
    - TODO: finish AvdConnectionMonitor
      
---- END BACKLOG ----
   
VERSION: 0.0.3

2017-12-05 TUE:
[PTS]
- Updated gradle config to support AndroidStudio v3.1 Canary4.


2017-12-04 MON:
2017-12-01 FRI:
[PTS]
- in-progress: FAS-39: User should be able to see Bluetooth 
        availability details on the DevicePairing scene
    - BluetoothAvailabilityWatcher.
    - BluetoothStatus
    - ResourceHelper
    - Added activate/deactivate methods in BluetoothConnector


2017-11-30 THU:
[PTS]
- DONE: Purge expired shipment data from Sqlite.
    - Clear SQLite cache of all data that is more than 7 days old.
    - SqliteShipmentRepo.purgeExpiredData
    
    
- FIXED: Made AndroidDevice params nullable.
    - This was causing the app to crash on devices that have no sim card in them.

2017-11-29 WED:
[PTS]
- DONE: Add support for Logentries.

- Added Keyboard scanner as PairingOperator in DeviceConnector.
 
2017-11-28 TUE:
[PTS]
- FIXED: (Bug) KeyboardScannerKeyEventConverter only reports a single barcode
            scan and then ignores all KeyEvents for subsequent scans. (2017-11-21)
      - Added test: KeyboardKeyEventToBarcodeConverterAndroidTest.it_can_process_multiple_barcodes
      
- Changed pairing connection timeout time in DeviceConnector from 5s to 10s.

 
2017-11-27 MON:
[PTS]
- DONE: Un-Pair with connected device.
    - PathfinderPairingOperator.disconnectFrom()
    
- DONE: Change ScannerConfig to only read Code128.
    - Changed PathfinderPairingTask to execute each config change
        over the AVD API in a beginSetSession/endSetSession 
        group, so that each config parameter is set set on its
        own. 
    - PathfinderScanner.executeConfigUpdate:
        Executes config updates for the API by pushing param session 
        changes for each param being updated.
    - PathfinderScanner.tryApiTask:
        Executes each param change sent to the API, catching ApiExecptions
        that are raised and re-trying to set the param if 
        the Bluetooth socket is busy.

2017-11-24 FRI:
[PTS]
- DONE: Record real Android DeviceInfo in AuthSetup API.

- DONE: Filter out no-reads (NR) from Scanner.

- DONE: Search by Barcode in Shipments, not by tracking number.
    - Receive barcode from ShipmentApi.
    - Add barcode to Shipment model.
        - Upgraded ShipmentModel to v3.
    - Search by barcode in SqliteShipmentRepo.

- DONE: De-bounce ScanEvents in PathfinderScanner.
    
- Changed "LabelShipment.lnt" to print labels with 3 rows.

2017-11-23 THU:
[PTS]
- DONE: Print labels with Pathfinder Scanner.
    
2017-11-21 TUE:
[PTS]
- DONE: Pair on background thread.
    - Added method: ConnectableDeviceCard.setupConnectButton

- DONE: Store Shipment address in SQLite.
    - Migrate SQLite to include Address column.
    - Added teardown method for ShipmentDbOpenHelper singleton.
    - Added Address and ErrorMessage to ScanningSceneView.

- Added transition to ScanningSceneView.

- TODO: KeyboardScannerKeyEventConverter only reports a single barcode
            scan and then ignores all KeyEvents for subsequent scans. (2017-11-21)
    
2017-11-20 MON:
[PTS]
- PENDING: Add support for listening to input from Keyboard scanner.
    - Listening for Symbol scanner events in KeyboardScannerKeyEventListener.
    - TODO: ie.fastway.scansort.device.scanner.KeyboardScannerKeyEventListenerTest
    
- DONE: Added Bluetooth ConnectionState icon to ConnectableDeviceCard.
    - New lib: 'com.joanzapata.iconify:android-iconify-material:2.2.2'
    
- Initial Release to Play Store.
        
2017-11-17 FRI:
[PTS]
- DONE: Add support for connecting to Bluetooth "keyboard" scanner.
    - Scanner: Symbol
    - Added bonded devices to deviceRegistry in DeviceConnector.
    
- DONE: NativeBluetoothConnector turns on Bluetooth if it is not turned on.
    
    
2017-11-16 THU:
[PTS]
- Changed  SqliteShipmentRepo.ShipmentRowParser.parseRow to accept null
    CF and RF.
    
- DONE: Export ScanEventLog data via API. 
    - ScanEventExporter
    - TEST: ScanEventExporterTest
    - Fixed ApiDateTimeConverter, was converting to date instead of timesamp.
        
- Wrapped SqliteShipmentRepo insert in a DB transaction. 

- Fixed ShipmentCatalog pagination error.


2017-11-15 WED:
[PTS]
- Scheduling data refresh immediately when isPaginated is true.
- Changed data download schedule period from 60sec to 30sec.

2017-11-14 TUE:
[PTS]
- DONE Add API endpoint to send scan events.

- DONE: Add onBackPressListener to MainSceneCoordinator so
        that the PathfinderConfigScene is hidden when the user 
        presses the back button.
    - Added "DONE" button to the pathfinder scanner scene.
    - Registered MainSceneCoordinator as onBackPressListener.
    

2017-11-13 MON:
[PTS]
- DONE: Show config screen to control the AveryDennison scanner.
    - PathfinderConfigPresenter
    - PathfinderConfigView
    - Added config setup methods to PathfinderScanner class.

- DONE: Ensure SQLite update occurs on background thread.
    - ShipmentCatalogImporter uses Scheduler.io for ShipmentRepo.
    

    
2017-11-09 THU:
[PTS]
- DONE: Loop the Shipment data importer so that it keeps trying
        to download shipment data on a schedule.
    - Triggering scheduled import of Shipment data when previous 
        import task finished.
    
     
    
2017-11-08 WED:
[PTS]
- DONE: Show DeviceDiscovery spinner.

- DONE: Show scanning details on Scanning Scene.
    - Changed view layout in `scene_scanning.xml`.

- DONE: Show Networking info bar at top of MainActivity.
    - Widget: ie.fastway.scansort.views.networkinfo.NetworkingInfoBar
    - WaitingInfoPresenter
    - Created EventPublisher to control the threads events are published on.
    - Changed  AppSessionContext.eventBus to EventPublisher.
    - Creating waitingEvent update (UserWaitingEventUpdate.Factory.onEventFinished)
        in ShipmentDeviceManager when importing data in SQLite.
        
    
2017-11-07 TUE:
[PTS]
- DONE: Keep track of Shipment updated_at timestamps. 
    - Find last known updated at timestamp in SqliteShipmentRepo.
    
    
- DONE: Connect to real backend API rather than mock API. 

    
2017-11-06 MON:
[PTS]
- DONE: Connect to real Bluetooth AveryDennison scanner.
    - Changed ScanningDeviceRegistry from a MutableList to a simple
        add/contains interface backed by a map and a list.
    - Changed ScanningDeviceRegistry to a PowDroid Publisher.
    - Registered for new IntentFilter actions in DeviceConnector Factory.
    - Added "Search" button in DevicePairing scene.
    - Created sealed class DeviceDiscoveryEvent for otto events.
    
   
- Imported jniLib `app/main/jniLibs/armeabi/libcompanion-api.so` 
    for AveryDennison API.
    
2017-11-03 FRI:
[PTS]
- DONE: Create ShipmentDataApi for downloading shipment label
            info and CF/RF data.
    - SQLite cache for Shipment data.
    - ie.fastway.scansort.shipment.repo.SqliteShipmentRepo
    - TEST: SqliteShipmentRepoTest
    - ie.fastway.scansort.shipment.repo.SqliteShipmentRepo.ShipmentRowParser
    - Added library: org.jetbrains.anko:anko-sqlite
          
  
2017-11-02 THU:
[PTS]
- PENDING: Create ShipmentDataApi for downloading shipment label
        info and CF/RF data.
    - Calling ShipmentDataManager.start() when 
        Auth is available in AppSessionService.

- Added Otto EventBus library: 'com.squareup:otto:1.3.8'
- Added eventBus to:
    - ie.fastway.scansort.lifecycle.AppSessionContext
    - ie.fastway.scansort.lifecycle.AppSessionProvider (singleton)  

- Subscribing to ScanEvents on ScanningScenePresenter.

  
2017-11-01 WED:
[PTS]
- in-progress: integrate a mock scanner that is triggered by a 
    button in the UI.
    - ScanningSceneView
    - ie.fastway.scansort.device.mocking.UiButtonMockScanner
    
- PathfinderScanner
- MockBluetoothConnector
- NativeBluetoothConnector
- PairingOperator
- PathfinderPairingTask
  
2017-10-31 TUE:
[PTS]
- DONE: Show the DevicePairingScene when the user is Auth'd OK.
    - DevicePairingPresenter
  

- Integrate Mock AuthApi with Renovate.
    - AuthApiMocker
    

2017-10-27 FRI
[PTS]
- Moved Equniox time utils to new module.
- Moved Renovate API service to new module.


2017-10-26 THU
[PTS]
- MainSceneCoordinator
- AppSesssionService
- AuthSetupManager
- FastwayApi
- DeviceConnector
- ShipmentDataManager


2017-10-23 MON
[PTS]
- Imported AveryDennison SDK jar.
- Imported Logistio's base Android project.


