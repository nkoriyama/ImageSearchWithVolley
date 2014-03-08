package org.nkoriyama.imagesearchwithvolley.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "ItemLookupResponse", strict = false)
public class AmazonPhotoResponse {
    public boolean isOK() {
        return (items != null && items.request.isValid);
    }
    public List<Item> getPhotoInfoList() {
        return items.items;
    }

    @Element(name = "Items")
    Items items;
    @Element(name = "OperationRequest")
    OperationRequest operationRequest;
}

@Root(strict = false)
class Items {
    @ElementList(entry = "Item", inline = true)
    List<Item> items;
    @Element(name = "MoreSearchResultsUrl", required = false)
    String moreSearchResultUrl;
    @Element(name = "Request")
    Request request;
    @Element(name = "TotalPages")
    int totalPages;
    @Element(name = "TotalResults")
    int totalResults;
}

@Root(strict = false)
class Item implements PhotoInfo {

    @Override
    public String getImageUrl() {
        if (largeImage != null)
            return largeImage.url;
        else
            return null;
    }

    @Override
    public String getThumbnailUrl() {
        if (mediumImage != null)
            return mediumImage.url;
        else
            return null;
    }

    @Override
    public String getTitle() {
        String title = "";

        if (itemAttributes != null) {
            title = itemAttributes.title;

            if (itemAttributes.binding != null) {
                title += " Binding:["  + itemAttributes.binding + "]";
            }
            if (itemAttributes.actors != null) {
                title += " Actor:[";
                boolean isFirst = true;
                for (String actor : itemAttributes.actors) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        title += ", ";
                    }
                    title += actor;
                }
                title += "]";
            }
            if (itemAttributes.artists != null) {
                title += " Artist:[";
                boolean isFirst = true;
                for (String artist : itemAttributes.artists) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        title += ", ";
                    }
                    title += artist;
                }
                title += "]";
            }
            if (itemAttributes.authors != null) {
                title += " Author:[";
                boolean isFirst = true;
                for (String author : itemAttributes.authors) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        title += ", ";
                    }
                    title += author;
                }
                title += "]";
            }
            if (itemAttributes.directors != null) {
                title += " Director:[";
                boolean isFirst = true;
                for (String director : itemAttributes.directors) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        title += ", ";
                    }
                    title += director;
                }
                title += "]";
            }

            Price price = null;
            if (offers != null && offers.offers != null) {
                for (Offer offer : offers.offers) {
                    if (offer.offerAttributes.condition.equals("New")) {
                        price = offer.offerListing.price;
                    }
                }
            }
            if (price == null && offerSummary != null) {
                price = offerSummary.LowestNewPrice;
            }
            if (price == null) {
                price = itemAttributes.listPrice;
            }
            if (price != null) {
                title += " Price:["  + price.formattedPrice + "]";
            }

            title += " ASIN:["  + asin + "]";
            if (itemAttributes.ean != null) {
                title += " JAN:["  + itemAttributes.ean + "]";
            }
        }

        return title;
    }

    @Override
    public String getShareSubject() {
        String shareSubject = "";
        if (itemAttributes != null) {
            shareSubject = "[ImageSearchWithVolley] " +
                    ((itemAttributes.title.length() > 60) ?
                            itemAttributes.title.substring(0, 60) :
                            itemAttributes.title);
        }
        return shareSubject;
    }

    @Override
    public String getShareText() {
        String shareText = "Amazon";

        if (itemAttributes != null) {
            shareText += System.getProperty("line.separator") + "Title:[" + itemAttributes.title + "]";

            shareText += System.getProperty("line.separator") + "Image URL:["  + getImageUrl() + "]";
            shareText += System.getProperty("line.separator") + "Product URL:["  + detailPageURL + "]";

            if (itemAttributes.binding != null) {
                shareText += System.getProperty("line.separator") + "Binding:["  + itemAttributes.binding + "]";
            }
            if (itemAttributes.actors != null) {
                shareText += System.getProperty("line.separator") + "Actor:[";
                boolean isFirst = true;
                for (String actor : itemAttributes.actors) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        shareText += ", ";
                    }
                    shareText += actor;
                }
                shareText += "]";
            }
            if (itemAttributes.artists != null) {
                shareText += System.getProperty("line.separator") +  "Artist:[";
                boolean isFirst = true;
                for (String artist : itemAttributes.artists) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        shareText += ", ";
                    }
                    shareText += artist;
                }
                shareText += "]";
            }
            if (itemAttributes.authors != null) {
                shareText += System.getProperty("line.separator") + "Author:[";
                boolean isFirst = true;
                for (String author : itemAttributes.authors) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        shareText += ", ";
                    }
                    shareText += author;
                }
                shareText += "]";
            }
            if (itemAttributes.directors != null) {
                shareText += System.getProperty("line.separator") + "Director:[";
                boolean isFirst = true;
                for (String director : itemAttributes.directors) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        shareText += ", ";
                    }
                    shareText += director;
                }
                shareText += "]";
            }

            Price price = null;
            if (offers != null && offers.offers != null) {
                for (Offer offer : offers.offers) {
                    if (offer.offerAttributes.condition.equals("New")) {
                        price = offer.offerListing.price;
                    }
                }
            }
            if (price == null && offerSummary != null) {
                price = offerSummary.LowestNewPrice;
            }
            if (price == null) {
                price = itemAttributes.listPrice;
            }
            if (price != null) {
                shareText += System.getProperty("line.separator") + "Price:["  + price.formattedPrice + "]";
            }

            shareText += System.getProperty("line.separator") + "ASIN:["  + asin + "]";
            if (itemAttributes.ean != null) {
                shareText += System.getProperty("line.separator") + "JAN:["  + itemAttributes.ean + "]";
            }
        }

        return shareText;
    }

    @Element(name = "ASIN")
    String asin;
    @Element(name = "DetailPageURL", required = false)
    String detailPageURL;
    @ElementList(name = "ImageSets", required = false)
    List<ImageSet> imageSets;
    @Element(name = "ItemAttributes", required = false)
    ItemAttributes itemAttributes;
    @ElementList(name = "ItemLinks", required = false)
    List<ItemLink> itemLinks;
    @Element(name = "LargeImage", required = false)
    Image largeImage;
    @Element(name = "MediumImage", required = false)
    Image mediumImage;
    @Element(name = "OfferSummary", required = false)
    OfferSummary offerSummary;
    @Element(name = "Offers", required = false)
    Offers offers;
    @Element(name = "SmallImage", required = false)
    Image smallImage;
}

@Root(strict = false)
class ImageSet {
    @Attribute(name = "Category")
    String category;
}

@Root(strict = false)
class ItemAttributes {
    @ElementList(entry = "Actor", inline = true, required = false)
    List<String> actors;
    @ElementList(entry = "Artist", inline = true, required = false)
    List<String> artists;
    @ElementList(entry = "Author", inline = true, required = false)
    List<String> authors;
    @ElementList(entry = "Director", inline = true, required = false)
    List<String> directors;
    @Element(name = "Binding", required = false)
    String binding;
    @Element(name = "EAN", required = false)
    String ean;
    @Element(name = "Label", required = false)
    String label;
    @Element(name = "ListPrice", required = false)
    Price listPrice;
    @Element(name = "ProductGroup", required = false)
    String productGroup;
    @Element(name = "Publisher", required = false)
    String publisher;
    @Element(name = "ReleaseDate", required = false)
    String releaseDate;
    @Element(name = "Studio", required = false)
    String studio;
    @Element(name = "Title", required = false)
    String title;
}

@Root(strict = false)
class ItemLink {
    @Element(name = "Description")
    String description;
    @Element(name = "URL")
    String url;
}

@Root(strict = false)
class Price {
    @Element(name = "Amount")
    int amount;
    @Element(name = "CurrencyCode")
    String currencyCode;
    @Element(name = "FormattedPrice")
    String formattedPrice;
}

@Root(strict = false)
class Image {
    @Element(name = "Height")
    int height;
    @Element(name = "URL")
    String url;
    @Element(name = "Width")
    int width;
}

@Root(strict = false)
class OfferSummary {
    @Element(name = "LowestCollectiblePrice", required = false)
    Price LowestCollectiblePrice;
    @Element(name = "LowestNewPrice", required = false)
    Price LowestNewPrice;
    @Element(name = "LowestRefurbishedPrice", required = false)
    Price LowestRefurbishedPrice;
    @Element(name = "LowestUsedPrice", required = false)
    Price LowestUsedPrice;
    @Element(name = "TotalCollectible")
    int totalCollectible;
    @Element(name = "TotalNew")
    int totalNew;
    @Element(name = "TotalRefurbished")
    int totalRefurbished;
    @Element(name = "TotalUsed")
    int totalUsed;
}

@Root(strict = false)
class Offers {
    @Element(name = "MoreOffersUrl")
    String moreOffersUrl;
    @ElementList(entry = "Offer", inline = true, required = false)
    List<Offer> offers;
    @Element(name = "TotalOfferPages")
    int totalOfferPages;
    @Element(name = "TotalOffers")
    int totalOffers;
}

@Root(strict = false)
class Offer {
    @Element(name = "OfferAttributes")
    OfferAttributes offerAttributes;
    @Element(name = "OfferListing")
    OfferListing offerListing;
}

@Root(strict = false)
class OfferAttributes {
    @Element(name = "Condition")
    String condition;
}

@Root(strict = false)
class OfferListing {
    @Element(name = "AmountSaved", required = false)
    Price amountSaved;
    @Element(name = "Availability", required = false)
    String availability;
    @Element(name = "AvailabilityAttributes", required = false)
    AvailabilityAttributes availabilityAttributes;
    @Element(name = "IsEligibleForSuperSaverShipping", required = false)
    int isEligibleForSuperSaverShipping;
    @Element(name = "OfferListingId")
    String offerListingId;
    @Element(name = "PercentageSaved", required = false)
    int percentageSaved;
    @Element(name = "Price")
    Price price;
}

@Root(strict = false)
class AvailabilityAttributes {
    @Element(name = "AvailabilityType")
    String availabilityType;
    @Element(name = "MaximumHours")
    int maximumHours;
    @Element(name = "MinimumHours")
    int minimumHours;
}

@Root(strict = false)
class Request {
    @Element(name = "IsValid")
    Boolean isValid;
}

@Root(strict = false)
class OperationRequest {
    @ElementList(name = "Arguments")
    List<Argument> arguments;
    @ElementList(name = "HTTPHeaders")
    List<Header> headers;
    @Element(name = "RequestId")
    String requestId;
    @Element(name = "RequestProcessingTime")
    String requestProcessingTime;
}

@Root(strict = false)
class Argument {
    @Attribute(name = "Name")
    String name;
    @Attribute(name = "Value")
    String value;
}

@Root(strict = false)
class Header {
    @Attribute(name = "Name")
    String name;
    @Attribute(name = "Value")
    String value;
}
