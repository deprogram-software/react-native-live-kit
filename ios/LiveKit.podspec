#
#  Be sure to run `pod spec lint LiveKit.podspec' to ensure this is a
#  valid spec and to remove all comments including this before submitting the spec.
#
#  To learn more about Podspec attributes see http://docs.cocoapods.org/specification.html
#  To see working Podspecs in the CocoaPods repo see https://github.com/CocoaPods/Specs/
#

Pod::Spec.new do |s|


  s.name         = "LiveKit"
  s.version      = "0.2.0"
  s.summary      = "A short description of LiveKit."

  s.description  = <<-DESC
                   DESC

  s.homepage     = "https://github.com/ltjin/react-native-live-kit"
  s.license      = "MIT"
  s.author             = { "jinlongtao" => "bajiaoweiyu@163.com" }

  # ――― Platform Specifics ――――――――――――――――――――――――――――――――――――――――――――――――――――――― #
  #
  #  If this Pod runs only on iOS or OS X, then specify the platform and
  #  the deployment target. You can optionally include the target after the platform.
  #

  s.platform     = :ios, "7.0"

  s.source       = { :git => "https://github.com/ltjin/react-native-live-kit.git", :tag => "master" }

  s.source_files  = "LiveKit/**/*.{h,m}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "PLMediaStreamingKit"
  s.dependency "PLPlayerKit"

end
