# Prayer location, Hijri adjustment and permissions

Prayer requests use Aladhan's coordinate timings endpoint with latitude, longitude,
IANA timezone, date and calculation method. Automatic methods are Egyptian General
Authority of Survey for Egypt, Umm Al-Qura for Saudi Arabia, ISNA for the US/Canada,
and Muslim World League elsewhere. Settings permits a method override. This remains
an online calculation service with an exact-request local cache, not an offline
astronomical calculator. The cache never substitutes another date or location.

Home requests current foreground location on resume and refreshes every five minutes
while resumed. Location work stops on pause/disposal. Android uses a cancellable
current-location request and accepts a recent last fix only if the fresh request is
unavailable. Both platforms reverse-geocode the country. If location or geocoding
fails, the last saved location or explicitly selected city is used and named on Home.
With neither, Home offers Settings rather than inventing Cairo or scheduling its
prayer times. The manual city selector includes eleven cities; GPS supports other
locations. Device timezone is used for Android GPS, while iOS also reads the
placemark timezone; manual cities have explicit IANA zones.

Changing location/method invalidates the request identity and refreshes Home and
Android alarms. Foreground GPS jitter below roughly half a kilometre is ignored.
Android refreshes scheduling after boot, date/clock/timezone changes, and changes
to permission status. Existing valid alarms survive an unchanged app launch.
Schedulers use the captured request timezone and honor existing category switches.
Without exact-alarm access, reminders use inexact idle-capable scheduling and may
arrive later. Quran reminder scheduling is owned by the worker coroutine rather
than an independent scope, so replaced work cannot continue that coroutine.

## Hijri dates

Settings stores a device-wide `hijriDateOffset` from -2 through +2, default zero.
Android ICU and iOS Foundation use Umm Al-Qura and apply the adjustment using calendar
day arithmetic, including month/year boundaries. Home observes the saved setting.
`HijriCalendar`, `SeasonalThemeManager` and `RamadanCalendar` share the adjusted date.
Season classification covers Ramadan, Eid al-Fitr (days 1–3 of Shawwal), the first
eight days of Dhul Hijjah, Arafah (9), and Eid al-Adha (10–13). Ramadan day is null
outside Ramadan and 1–30 inside it. The repository previously contained no seasonal
manager or Ramadan-calendar screen; these shared APIs and the Home seasonal snapshot
provide the central calculation without introducing a new calendar screen.

## Permissions

Onboarding offers Location, Notifications, Exact Alarms (Android), then Battery
exemption (Android). Every permission can be skipped independently. Settings shows
live system statuses separately from the app's notification-category switches.
Android requests fine/coarse location together; approximate access is accepted.
Skipping notifications suppresses the separate returning-user notification prompt;
Settings remains available for a later explicit request. Permanent runtime denials open app settings. Exact-alarm and battery access use
system settings, with an app-settings fallback if an OEM omits the specialized UI.

The requester records the outstanding permission across activity recreation and
rechecks on ON_RESUME and Activity Result. The onboarding ViewModel records a
returned prompt even if access remains denied: Continue then advances instead of
reopening it. Granting access advances automatically, and completion is idempotent.
iOS offers only its applicable Location and Notification permissions and uses the
native CoreLocation/UserNotifications APIs. The existing iOS local-alarm adapter
remains unsupported; granting notification permission does not implement iOS alarms.

## Verification limits

Android/shared unit tests cover permission progression, the battery-return regression,
saved configuration, country methods, invalid coordinates/timezones/offsets, request
identity and seasonal boundaries. Android debug and common metadata compilation
check shared APIs. Native iOS compilation and real permission dialogs need macOS or
devices. A live Aladhan check was blocked by this environment's outbound proxy, so
live-service timings have not been independently compared here.
