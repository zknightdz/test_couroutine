DataBinding: Là thư viện hỗ trợ việc liên kết dữ liệu tự động giữa data sources và UI elements.

	- Enable DataBinding:
	Trong app/build.gradle
		android {
			...
			dataBinding {
				enabled = true
			}
		}
		
	- Cấu trúc của DataBinding layout:
	<layout ...>
		<data>
			 
			<variable
				name="..."
				type="..." />
		</data>
	 
		<LinearLayout ...>
		   <!-- YOUR LAYOUT HERE -->
		</LinearLayout>
	</layout>
	
	- Bind data lên View:
	Trong layout file:
		<variable name="user" type="com.example.User" />
		...
		<TextView 
			android:text="@{user.firstName}" />
		
	Trong Activity: ActivityMainBinding là class được tự động generated.
		override fun onCreate(savedInstanceState: Bundle?) {
			super.onCreate(savedInstanceState)

			val binding: ActivityMainBinding = DataBindingUtil.setContentView(
					this, R.layout.activity_main)

			binding.user = User("Test", "User")
		}

	- DataBinding, ngoài việc bind data lên View, còn có thể handle event của View (onClick, onLongClick,...)
		<variable name="handlers" type="com.example.MyHandlers"/>
		...
		<TextView 
           android:onClick="@{handlers::onClickFriend}"/>

		   
		   
-------------------------------------------------------------

RxJava: Là 1 thư viện hỗ trợ việc xử lý đa luồng trong Java và Android. Hoạt động dựa trên Obsever pattern.
Có 2 object chính: Observable và Observer.
Observable: phát ra các event.
Observer: lắng nghe các event được Observable phát ra.

- Cài đặt: 
	Trong app/build.gradle
		implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
		implementation "io.reactivex.rxjava2:rxjava:2.2.0"

- subscribeOn():
	Chỉ thị thread thực hiện nhiệm vụ.
	Có thể nằm ở vị trí tùy ý.
	Khi có nhiều subscribeOn() thì các statement chỉ có thể thực hiện trên Thread được khai báo ở subscribeOn() đầu tiên.
	Khi gọi subscribeOn() thì không thay đổi Thread của observeOn()
- observeOn():
	Chuyển đổi Thread sử dụng khi gặp phải observeOn(). Các statement ở phía sau observeOn() đều chạy trên Thread khai báo ở observeOn().
	Thông thường trên Android thread được khai báo là Main(UI) thread - AndroidSchedulers.mainThread().
	
		Observable.just("long", "longer", "longest")
			.doOnNext { c -> println("processing item on thread " + Thread.currentThread().name) }
			.subscribeOn(Schedulers.newThread())
			.map { it.length }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { length -> println("item length " + length + " received on thread " + Thread.currentThread().name) }
	
	
	
-map():
	Chuyển đổi output cho từng item bằng cách áp dụng 1 function cho item đó(Chuyển đổi 1->1).
	
		Observable.just(1, 2, 3, 4, 5)
			.map { "item $it" }
			.subscribe {
				println(it)
			}
-flatMap():
	Chuyển đổi 1 item thành 1 luồng khác (Observable -> Observables).
	Thường được sử dụng trên các operations bất đồng bộ.
	Có thể sử dụng một Scheduler mới bằng cách sử dụng subscribeOn() để xử lý các operations đó.

	    val flatMapObs = Observable.just(7, 8, 9)
        Observable.just(1, 2, 3, 4, 5)
            .flatMap { flatMapObs }
            .subscribe {
                println(it)
            }
-filter():
	Lọc ra các item thỏa mãn điều kiện bên trong method filter(). Các giá trị không thỏa mãn sẽ không được truyền đi.
		Observable.just("ss", "abc", "ccc", "abs", "saa")
			.filter { it.contains("s") }
			.subscribe {
				println(it)
			}
			
----------------------------------------			
KODEIN:
	
	- singleton: Tạo đối tượng 1 lần duy nhất
	- eagerSingleton: giống singleton nhưng tạo sớm hơn(khi kodein mới khởi tạo).
	- factory: khi cần dùng thì mới khởi tạo, có tham số - sử dụng với Context hoặc chuyển đổi tham số thành giá trị khác
	- provider: khi cần dùng thì mới khởi tạo, không có tham số
	- instance: khởi tạo bằng 1 đối tượng khởi tạo sẵn (singleton).
	...
	
	Tag: sử dụng để bind nhiều đối tượng cùng kiểu.( không thể bind 2 object cùng tag, không thể bind 2 object không có tag)
	
		val kodein = Kodein {
			bind<Dao>("dao1") with singleton { MongoDao() }
			bind<Dao>("dao2") with singleton { MongoDao() }
		}
	
Modules: gộp 1 nhóm các object thành 1 module để có thể sử dụng ở container khác.

	// Khởi tạo Kodein module
    val jdbcModule = Kodein.Module(name = "jdbcModule") {
        bind<Dao>() with singleton { JdbcDao() }
    }
	
	import(jdbcModule) //sử dụng để khai báo trong container khác
	
	val kodein = Kodein {
		import(jdbcModule)
		bind<Controller>() with singleton { Controller(instance()) }
		bind<Service>() with singleton { Service(instance(), "myService") }
	}


Composition: sử dụng nhiều container lồng nhau

	val persistenceContainer = Kodein {
		bind<Dao>() with singleton { MongoDao() }
	}
	val serviceContainer = Kodein {
		extend(persistenceContainer)
		bind<Service>() with singleton { Service(instance(), "myService") }
	}

Overriding: ghi đè 1 object đã được bind

	bind<Dao>() with singleton { MongoDao() }
	bind<Dao>(overrides = true) with singleton { InMemoryDao() }
	
Injector: inject kodein to Activity

	// khởi tạo Injector
	val injector = KodeinInjector()
	// inject kodein to Activity
	injector.inject(kodein)
	// lấy ra instance
	val user by injector.instance<User>()
		
			
-----------------------------------------------
Tìm hiểu waku:

	Tổ chức folder theo chức năng. mỗi module gồm: contract, interactor, presenter, router, view.
	. Contract chứa các interface: View, Presentation, UseCase, InteractorOutput, Wireframe.
		- In Wireframe: chứa method assembleModule() để inject DI, khởi tạo các Interactor, InteractorOutput, Presenter, Router, View, Service, DataSource...
		- In view: 1 module chứa 1 activity, 1 hoặc nhiều fragment.
		- In Interactor: có 1 CompositeDisposable quản lý các Disposable. Khi fragment destroy thì CompositeDisposable cũng dispose.
		
			
			
			
-------------------------------------------------------

* Peer to Peer: 	
	- Kết nối giữa các client không thông qua central server. Mỗi client vừa đóng vị trí client và server.
	-
	
* PeerConnectionFactory:
	- PeerConnectionFactory is used to create PeerConnection, MediaStream and MediaStreamTrack objects.
	
	- Creating PeerConnectionFactory
		Trước khi khởi tạo PeerConnectionFactory, khởi tạo InitializationOptions.
	
		val fieldTrials = (PeerConnectionFactory.VIDEO_FRAME_EMIT_TRIAL + "/" + PeerConnectionFactory.TRIAL_ENABLED + "/")
		//Create InitializationOptions
		val options = InitializationOptions.builder(application)
			  .setFieldTrials(fieldTrials)
			  .setEnableVideoHwAcceleration(videoAccelerationEnabled)
			  .createInitializationOptions()
		PeerConnectionFactory.initialize(options) 
		factory = PeerConnectionFactory(PeerConnectionFactory.Options())
		val rootEglBase = EglBase.create()
		factory?.setVideoHwAccelerationOptions(rootEglBase.eglBaseContext, rootEglBase.eglBaseContext)	
	
* PeerConnection:
	- PeerConnection dùng để thiết lập peer to peer connection.
	
	- Khởi tạo 1 PeerConnection#Observer
	
		val peerConnectionObserver = object : PeerConnection.Observer {
			// Triggered when the IceConnectionState changes. {localPeer}
		   override fun onIceCandidate(iceCandidate: IceCandidate) {
			  localIceCandidatesSource.onNext(iceCandidate)
		   }
		   // Triggered when media is received on a new stream from remote peer. {localPeer, remotePeer}
		   override fun onAddStream(mediaStream: MediaStream) {
			   mediaStream.addRenderer(remoteRenderer)
		   }
		   ...
		}
		
	- Khởi tạo PeerConnection:
		val peerIceServers = ArrayList<>()
		// Config ICE
        val rtcConfig = PeerConnection.RTCConfiguration(peerIceServers)
        val pcConstraints = MediaConstraints()
	
		peerConnection = factory?.createPeerConnection(rtcConfig, pcConstraints, peerConnectionObserver)
	
	- createOffer: Tạo video call... Sử dụng SdpObserver.
		#setLocalSdp:
			fun setLocalSdp(sdp: SessionDescripton) {
			   peerConnection.setLocalDescription(object : SdpObserver {
				  /** Called on success of Set{Local,Remote}Description(). */	
				  override fun onSetSuccess() {
					 api.sendSdp(peerConnection.localDescription)
					 drainIceCandidates()
				  }
				  ...
			   }, sdp)
			}
		#createOffer:
			fun createOffer() {
				peerConnection.createOffer(object : SdpObserver {
				    ** Called on success of Create{Offer,Answer}(). */
				    override fun onCreateSuccess(sdp: SessionDescription) {
						setLocalSdp(sdp)
				    }
				    ...
				    }, getPeerConnectionConstraints())
			}
			
	- CreateAnswer: Trả lời cuộc gọi
		fun createAnswer() {
		   peerConnection?.createAnswer(object : SdpObserver {
			  override fun onCreateSuccess(sdp: SessionDescription) {
				 setLocalSdp(sdp)
			  }
		   }, getPeerConnectionConstraints())
		}

* IceServer: 
	- ICE:  is used for connecting peer to peer.
	- IceServer config connection cho PeerConnection.
	- Một PeerConnection chứa list IceServer. Mỗi IceServer chứa các thông tin: url, username, credential.
	
		var restartConfig = { iceServers: [{
								  urls: "turn:asia.myturnserver.net",
								  username: "allie@oopcode.com",
								  credential: "topsecretpassword"
							  }]
		};

		myPeerConnection.setConfiguration(restartConfig);
	
	
	
* SDP in webrtc:
	SPD: is used to provide the metadata of the media content like resolution, encoding, bitrate, etc.
	
	
	
* MediaStream: Object chứa AudioTrack, VideoTrack
	- Create MediaStream:
	
		val localMediaStream = factory.createLocalMediaStream(MEDIA_ID)
		val audioSource = factory.createAudioSource(MediaConstraints())
		val audioTrack = factory.createAudioTrack(AUDIO_ID, audioSource)
		localMediaStream.addTrack(audioTrack)

		videoCapturer = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
		   createCameraCapturer(Camera2Enumerator(application))
		} else {
		   createCameraCapturer(Camera1Enumerator(videoAccelerationEnabled))
		}
		val videoTrack = factory.createVideoTrack("VideoTrack", factory.createVideoSource(videoCapturer))
		localMediaStream.addTrack(videoTrack)
			
	- CameraVideoCapturer:
	
		private fun createCameraCapturer(enumerator: CameraEnumerator): CameraVideoCapturer? {
		   val deviceNames = enumerator.deviceNames
		   for (deviceName in deviceNames) {
			  if (enumerator.isFrontFacing(deviceName)) {
				 val videoCapturer = enumerator.createCapturer(deviceName, null)
				 if (videoCapturer != null) {
					return videoCapturer
				 }
			  }
		   }

		   for (deviceName in deviceNames) {
			  if (!enumerator.isFrontFacing(deviceName)) {
				 Timber.d("Creating other camera capturer.")
				 val videoCapturer = enumerator.createCapturer(deviceName, null)
				 if (videoCapturer != null) {
					return videoCapturer
				 }
			  }
		   }
		   return null
		}
			
		
	- SurfaceViewRenderer:
	
		localViewRenderer.init(rootEglBase.getEglBaseContext(), null)
		localViewRenderer.setEnableHardwareScaler(true)
		localViewRenderer.setMirror(true)
		localViewRenderer.setScalingType(ScalingType.SCALE_ASPECT_FILL)
		val localVideoRenderer = VideoRenderer(localViewRenderer)
		videoTrack.addRenderer(localVideoRenderer)
		
		
		
		
		
		
flutter packages pub run build_runner build	

7713c9e0-6ddc-44c5-b749-02897517f742	

----------------------------------------------------------------
Coroutines

Coroutines do not replace threads, it’s more like a framework to manage it.

The exact definition of Coroutines: A framework to manage concurrency in a more performant and simple way with its lightweight thread which is written on top of the actual threading framework to get the most out of it by taking the advantage of cooperative nature of functions.

Dispatchers: Dispatchers helps coroutines in deciding the thread on which the work has to be done. There are majorly three types of Dispatchers which are as IO, Default, and Main. IO dispatcher is used to do the network and disk related work. Default is used to do the CPU intensive work. Main is the UI thread of Android. In order to use these, we need to wrap the work under the async function. Async function looks like below.

suspend: Suspend function is a function that could be started, paused and resume.

There are two functions in Kotlin to start the coroutines which are as follows:
	- launch{}
	- async{}
	
The difference is that the launch{} does not return anything and the async{}returns an instance of Deferred<T>, which has an await()function that returns the result of the coroutine.

Thay đổi scope thì implementation CoroutineScope,
		override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job 
		
Job có nhiệm vụ cancel background task
background task destroy khi activity destroy nếu định nghĩa scope
GlobalScope run background task cả khi app destroy

Exception Handling in Kotlin Coroutines

When Using launch

		val handler = CoroutineExceptionHandler { _, exception ->
			Log.d(TAG, "$exception handled !")
		}
		
		GlobalScope.launch(Dispatchers.IO + handler) {
			fetchUserAndSaveInDatabase() // do on IO thread
		}
		
		override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + handler

When Using async

		val deferredUser = GlobalScope.async {
			fetchUser()
		}
		try {
			val user = deferredUser.await()
		} catch (exception: Exception) {
			Log.d(TAG, "$exception handled !")
		}

Fresco sources | off site
(-)
- Huge size of library
- No Callback with View, Bitmap parameters
- SimpleDraweeView doesn't support wrap_content
- Huge size of cache
(+)
- Pretty fast image loader (for small && medium images)
- A lot of functionality(streaming, drawing tools, memory management, etc)
- Possibility to setup directly in xml (for example round corners)
- GIF support
- WebP and Animated Webp support


Picasso sources | off site
(-)
- Slow loading big images from internet into ListView
(+)
- Tinny size of library
- Small size of cache
- Simple in use
- UI is not freeze
- WebP support


Glide sources

(-)
- Big size of library
(+)
- Tinny size of cache
- Simple in use
- GIF support
- WebP support
- Fast loading big images from internet into ListView
- UI is not freeze
- BitmapPool to re-use memory and thus lesser GC events


Universal Image Loader sources

(-)
- Limited functionality (limited image processing)
- Project support has stopped since 27.11.2015
(+)
- Tinny size of library
- Simple in use


















