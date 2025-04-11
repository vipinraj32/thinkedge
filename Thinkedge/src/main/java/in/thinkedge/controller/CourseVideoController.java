package in.thinkedge.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import in.thinkedge.model.CourseVideo;
import in.thinkedge.model.ErrorResponse;
import in.thinkedge.service.Impl.CourseVideoServiceImpl;
import io.jsonwebtoken.io.IOException;
import jakarta.validation.Valid;

@RestController
public class CourseVideoController {
    private final String  VIDEO_URL="D:/video/";
    @Autowired
    private CourseVideoServiceImpl courseVideoServiceImpl;
  
    
    @PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/addVideo")
	public ResponseEntity<?> addVideo(@RequestParam("courseId")String courseId, @RequestParam("title") @Valid String tilte,@RequestParam("video") MultipartFile file){
		 
		Boolean status=courseVideoServiceImpl.addCourseVideo(courseId,tilte,file);
			   Map<String,Object> response=new HashMap<>();
			   response.put("message", "Course Video Successfully Added");
			   response.put("status", HttpStatus.CREATED.value());
			   return ResponseEntity.status(HttpStatus.CREATED).body(response);   
	}
    
//    @GetMapping("/showVideo")
//    public ResponseEntity<?> getVideos(@RequestParam("courseId")String courseId) throws java.io.IOException{
////    	byte[] imageData=courseVideoServiceImpl.findById(id);
//    	List<CourseVideo> courseVideos=courseVideoServiceImpl.getAllCourseVideos(courseId);
//    	List<Map> responseList=new ArrayList<>();
//    	for(CourseVideo courseVideo:courseVideos) {
//    		Map<String, Object> response=new HashMap<>();
//    		response.put("video_id", courseVideo.getVideoId());
//    		response.put("title", courseVideo.getTitle());
//    		response.put("video_url", "http://localhost:8081/api/getVideo?filename="+courseVideo.getVideoUrl().substring(9));
//    		responseList.add(response);
//    	}
////    	return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("video/mp4")).body(imageData);
//    	return ResponseEntity.status(HttpStatus.OK).body(responseList);
//    }
    
    @GetMapping("/showVideo")
    public ResponseEntity<List<CourseVideo>> getVideos(@RequestParam("courseId")String courseId){
//    	byte[] imageData=courseVideoServiceImpl.findById(id);
    	List<CourseVideo> courseVideos=courseVideoServiceImpl.getAllCourseVideos(courseId);
    	
    	return new ResponseEntity<>(courseVideos,HttpStatus.OK);
    }
    
    @GetMapping("/getVideo")
    public ResponseEntity<Resource> getVideo(@RequestParam("filename")String filename){
    
    	 
    	
    	 try {
             Path filePath = Paths.get(VIDEO_URL+filename);
             Resource resource = new UrlResource(filePath.toUri());

             if (!resource.exists()) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
             }

             return ResponseEntity.ok()
                     .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                     .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                     .body(resource);
         } catch (MalformedURLException e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
         }
 
    }
    
     
    @GetMapping("/video")
    public ResponseEntity<?> getVideoByUrl(@RequestParam("video_url")String videoUrl) throws java.io.IOException{
    	byte[] videoData=courseVideoServiceImpl.getVideoByUrl(videoUrl);
    	return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("video/mp4")).body(videoData);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteVideo/{courseId}")
    public ResponseEntity<Map> removeVideo(@RequestParam("title")String title, @RequestParam("courseId")String courseId){
    	Boolean status=courseVideoServiceImpl.removeCourseVideo(title, courseId);
    	Map<String, Object>response=new HashMap<>();
    		response.put("message", "Video Removed Succesfully video_id:"+title);
    		response.put("status", HttpStatus.OK.value());
    		return ResponseEntity.status(HttpStatus.OK).body(response); 	
    }
}
