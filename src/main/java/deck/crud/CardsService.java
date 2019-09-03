package deck.crud;

import deck.image.generation.CardConfigurationProcessor;
import deck.image.generation.CardGenerationUnavailable;
import deck.image.generation.CardImagePrototype;
import deck.image.generation.CardPrototype;
import deck.model.Card;
import deck.model.CardImage;
import deck.model.Project;
import deck.model.Image;
import deck.repository.CardImageRepository;
import deck.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CardsService {

    private final CardConfigurationProcessor cardConfigurationProcessor;
    private final CardRepository cardRepository;
    private final CardImageRepository cardImageRepository;

    @Autowired
    public CardsService(CardConfigurationProcessor cardConfigurationProcessor, CardRepository cardRepository, CardImageRepository cardImageRepository) {
        this.cardConfigurationProcessor = cardConfigurationProcessor;
        this.cardRepository = cardRepository;
        this.cardImageRepository = cardImageRepository;
    }

    public void generateCardsForDeck(Project deck) {
        int imagesOnCard = deck.getImagesOnCard();
        List<CardPrototype> prototypes = null;
        switch (imagesOnCard) {
            case 5:
                prototypes = cardConfigurationProcessor.getPerFiveConfig();
                break;
            case 6:
                prototypes = cardConfigurationProcessor.getPerSixConfig();
                break;
            case 8:
                prototypes = cardConfigurationProcessor.getPerEightConfig();
                break;
        }

        if (Objects.requireNonNull(prototypes).size() > deck.getImages().size()) {
            throw new CardGenerationUnavailable("not enough images for cards generation");
        }

        List<Card> persistedCards = new ArrayList<>();
        for (int i = 0; i < prototypes.size(); i++) {
            Card card = new Card();
            card.setDeckId(deck.getId());
            persistedCards.add(card);
        }
        List<Card> cards = cardRepository.saveAll(persistedCards);

        List<Image> images = deck.getImages();
        for (int cardNumber = 0; cardNumber < cards.size(); cardNumber++) {
            Card card = cards.get(cardNumber);
            CardPrototype cardPrototype = prototypes.get(cardNumber);

            List<CardImage> cardImages = new ArrayList<>();
            for (int imageOnCardNumber = 0; imageOnCardNumber < cardPrototype.getImages().size(); imageOnCardNumber++) {
                CardImagePrototype cardImagePrototype = cardPrototype.getImages().get(imageOnCardNumber);

                CardImage cardImage = new CardImage();
                cardImage.setCardId(card.getId());
                cardImage.setImageId(images.get(cardImagePrototype.getImageId() - 1).getId());
                cardImage.setPositionX(cardImagePrototype.getX());
                cardImage.setPositionY(cardImagePrototype.getY());
                cardImage.setRotationAngle(cardImagePrototype.getRotationAngleMillirad());
                cardImage.setScaleFactor(cardImagePrototype.getScaleFactor());
                cardImages.add(cardImage);
            }
            cardImageRepository.saveAll(cardImages);

        }
    }

    public List<List<CardImage>> getDeckData(Project deck) {
        long id = deck.getId();
        List<Card> allByDeckId = cardRepository.findAllByDeckId(id);
        List<Long> ids = allByDeckId.stream().map(Card::getId).collect(Collectors.toList());
        List<CardImage> images = cardImageRepository.findAllByCardIdIn(ids);
        for (CardImage image : images) {
            int imageId = image.getId();
            image.setImageUrl(deck.getImages().get(imageId-1).getUrl());
        }
        Map<Long, List<CardImage>> imagesByCards = images.stream().collect(Collectors.groupingBy(CardImage::getCardId));
        return imagesByCards.entrySet().stream().map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public List<CardImage> persistCardImages(List<CardImage> cardImages){
        return cardImageRepository.saveAll(cardImages);
    }
}
