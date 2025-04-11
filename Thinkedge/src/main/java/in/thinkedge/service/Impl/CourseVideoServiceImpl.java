
package in.thinkedge.service.Impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.thinkedge.exception.CourseNotFoundException;
import in.thinkedge.exception.CustomIOException;
import in.thinkedge.exception.VideoAlreadyExistException;
import in.thinkedge.exception.VideoContentTypeException;
import in.thinkedge.exception.VideoNotFoundException;
import in.thinkedge.model.Course;
import in.thinkedge.model.CourseVideo;
import in.thinkedge.repository.CourseRepository;
import in.thinkedge.repository.CourseVideoRepository;
import jakarta.transaction.Transactional;

@Service
public class CourseVideoServiceImpl {

	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private CourseVideoRepository videoRepository;
	@Value("${project.manager}")
	private String FOlDER_URL;
	
	@Transactional
	public Boolean addCourseVideo( String courseId, String videoTitle, MultipartFile file) {
		if(!file.getContentType().equals("video/mp4")) {
			throw new VideoContentTypeException("Failed to upload video: '" + file.getContentType() + "' is not supported. Please upload a video with content type 'video/mp4'.");
		}
		Course course=courseRepository.findById(courseId).orElseThrow(()->new CourseNotFoundException("Course not found. Please enter a valid course ID"));
		CourseVideo courseVideo=videoRepository.findByTitleAndCourse(videoTitle,course).orElse(null);
		if(courseVideo!=null) {
			throw new VideoAlreadyExistException("Duplicate title detected. Kindly provide a unique title for your video.");
		}
		String filePath=FOlDER_URL+file.getOriginalFilename();
			CourseVideo videos=new CourseVideo();
			videos.setTitle(videoTitle);
			videos.setVideoUrl(filePath);
			try {
			file.transferTo(new File(filePath));
			}catch (IOException e) {
				throw new CustomIOException("Video upload failed: " + e.getMessage());
			}
			course.addVideo(videos);
			courseRepository.save(course);
			return true;
	}
	
	public List<CourseVideo> getAllCourseVideos(String courseId){
		
		List<CourseVideo> courseVideos=videoRepository.getAllVideosByCourseId(courseId);
//			String url=courseVideos.get().getVideoUrl();
//			byte[] videos=Files.readAllBytes(new File(url).toPath());
		List<CourseVideo> courseVideos2=new ArrayList<>();
		   for(CourseVideo courseVideo:courseVideos) { 
			   courseVideo.setVideoUrl("http://localhost:8081/api/getVideo?filename="+courseVideo.getVideoUrl().substring(9));
			   courseVideos2.add(courseVideo);
		   }
			return courseVideos2;
	}
	
	public byte[] getVideoByUrl(String video_url) throws java.io.IOException {
		byte[] videos=Files.readAllBytes(new File(video_url).toPath());
		return videos;
	}
	
	@Transactional
	public Boolean removeCourseVideo(String title, String courseid) {
		Course course=courseRepository.findById(courseid).orElseThrow(()-> new CourseNotFoundException("Course not found. Please enter a valid course ID"));
		CourseVideo courseVideo=videoRepository.findByTitleAndCourse(title, course).orElseThrow(()->new VideoNotFoundException("No video found with the specified title in the selected course"));
    			course.removeVideo(courseVideo);//Remove emp from dept
    			courseRepository.save(course);//For Consistency amongst JPA and SQL 
			    videoRepository.delete(courseVideo);
		        return true;
		
	}
	
	
}
