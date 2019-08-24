package deck.crud;

import deck.controller.ResourceNotFoundException;
import deck.dto.CreateDeckDTO;
import deck.model.Deck;
import deck.model.Image;
import deck.repository.DeckRepository;
import deck.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageService {


    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image submitNewAndGet(String imageUrl, Deck deck){
        Image image = new Image(imageUrl, deck);
        return imageRepository.save(image);
    }

    public Image submitNewAndGet(String imageUrl){
        Image image = new Image();
        image.setUrl(imageUrl);
        return imageRepository.save(image);
    }

    public List<Image> findAll(){
        return imageRepository.findAll();
    }

    public Image getById(long id){
        Optional<Image> byId = imageRepository.findById(id);
        if(!byId.isPresent()){
            throw new ResourceNotFoundException();
        }
        return byId.orElseGet(Image::new);
    }

    public Image updateImageAndGet(Long imageId, String newUrl) {
        Image image = imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image with id " + imageId + " not found"));
        image.setUrl(newUrl);
        return imageRepository.save(image);
    }
}
