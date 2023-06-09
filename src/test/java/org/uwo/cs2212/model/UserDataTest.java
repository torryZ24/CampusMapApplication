package org.uwo.cs2212.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uwo.cs2212.CampusMapApplication;
import org.uwo.cs2212.ConfigUtil;
import org.uwo.cs2212.CurrentUser;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Tingrui Zhang
 */
class UserDataTest {

    private UserData userData;
    private BaseMap baseMap;
    private FloorMap floorMap;
    private PointOfInterest poi;

    @BeforeEach
    void setUp() {
        userData = new UserData();
        baseMap = new BaseMap();
        baseMap.setName("Test BaseMap");
        floorMap = new FloorMap();
        floorMap.setName("Test FloorMap");
        poi = new PointOfInterest();
        poi.setName("Test POI");
        CurrentUser.setUsername("testUser");
        CurrentUser.setMapConfig(ConfigUtil.loadMapConfig(CampusMapApplication.class.getResource("map-config.json")));

        CurrentUser.setCurrentBaseMap(baseMap);
        CurrentUser.setCurrentFloorMap(floorMap);
        CurrentUser.setCurrentSelectedPoi(poi);
    }

    @Test
    void testFindUserLayerNotFound() {
        UserLayer userLayer = UserData.findUserLayer(baseMap, floorMap, userData);
        assertNull(userLayer);
    }

    @Test
    void testFindUserLayerWithNullUserData() {
        UserLayer userLayer = UserData.findUserLayer(baseMap, floorMap, null);
        assertNull(null);
    }

    @Test
    void testFindUserLayerWithNullUserLayers() {
        userData.setUserLayers(null);
        UserLayer userLayer = UserData.findUserLayer(baseMap, floorMap, userData);
        assertNull(userLayer);
    }

    @Test
    void testEmptyUserLayers() {
        userData.setUserLayers(new ArrayList<>());
        assertEquals(0, userData.getUserLayers().size());
    }

    @Test
    void testEmptyFavouritePois() {
        userData.setFavouritePois(new ArrayList<>());
        assertEquals(0, userData.getFavouritePois().size());
    }

    @Test
    void testUserLayersGetterAndSetter() {
        List<UserLayer> userLayers = new ArrayList<>();
        UserLayer userLayer = new UserLayer();
        userLayer.setName("Test UserLayer");
        userLayers.add(userLayer);
        userData.setUserLayers(userLayers);
        assertEquals(userLayers, userData.getUserLayers());
    }

    @Test
    void testAddPoi() {
        userData.addPoi(baseMap, floorMap, poi);
        UserLayer userLayer = userData.getUserLayers().get(0);
        PointOfInterest addedPoi = userLayer.getPoints().get(0);
        assertEquals(poi.getName(), addedPoi.getName());
    }

    @Test
    void testUpdateUserLayers() {
        userData.addPoi(baseMap, floorMap, poi);
        UserLayer userLayer = userData.getUserLayers().get(0);
        userLayer.setName("Updated Test UserLayer");
        userData.updateUserLayers(userLayer);
        UserLayer updatedUserLayer = userData.getUserLayers().get(0);
        assertEquals("Updated Test UserLayer", updatedUserLayer.getName());
    }

    @Test
    void testRemoveFavourite() {
        // Set up the current user configuration
        CurrentUser.setUsername("testUser");
        UserData userData = new UserData();
        CurrentUser.setUserData(userData);

        // Set up base map, floor map, and point of interest
        BaseMap baseMap = new BaseMap();
        baseMap.setName("TestBaseMap");
        FloorMap floorMap = new FloorMap();
        floorMap.setName("TestFloorMap");
        PointOfInterest poi = new PointOfInterest();
        poi.setName("TestFavouritePoi");
        poi.setFavourite(true);

        CurrentUser.setMapConfig(ConfigUtil.loadMapConfig(CampusMapApplication.class.getResource("map-config.json")));

        // Add a favourite POI
        userData.addPoi(baseMap, floorMap, poi);
        FavouritePoi favouritepoi = new FavouritePoi(baseMap.getName(), floorMap.getName(), null, poi.getName());
        userData.getFavouritePois().add(favouritepoi);
        // Remove the favourite POI
        userData.removeFavourite(poi, baseMap, floorMap);

        // Check if the favourite POI was removed
        assertFalse(userData.getFavouritePois().contains(favouritepoi));

        // Check if the favourite flag was changed
        assertFalse(poi.isFavourite());
    }

    @Test
    void testFindUserLayer() {
        userData.addPoi(baseMap, floorMap, poi);
        UserLayer userLayer = UserData.findUserLayer(baseMap, floorMap, userData);
        assertNotNull(userLayer);
        assertEquals("User layer", userLayer.getName());
    }

    @Test
    void testFavouritePoisGetterAndSetter() {
        List<FavouritePoi> favouritePois = new ArrayList<>();
        FavouritePoi favouritePoi = new FavouritePoi();
        favouritePoi.setPoiName("Test FavouritePoi");
    }
    // Additional test cases

    @Test
    void testAddPoiToExistingUserLayer() {
        // Add a POI to create a new UserLayer
        userData.addPoi(baseMap, floorMap, poi);

        // Add a new POI to the existing UserLayer
        PointOfInterest poi2 = new PointOfInterest();
        poi2.setName("Test POI 2");
        userData.addPoi(baseMap, floorMap, poi2);

        UserLayer userLayer = userData.getUserLayers().get(0);
        assertEquals(2, userLayer.getPoints().size());
        assertEquals(poi2.getName(), userLayer.getPoints().get(1).getName());
    }

    @Test
    void testUpdateNonExistingUserLayers() {
        UserLayer nonExistingUserLayer = new UserLayer();
        nonExistingUserLayer.setName("Non-existing UserLayer");
        userData.updateUserLayers(nonExistingUserLayer);
        assertFalse(userData.getUserLayers().contains(nonExistingUserLayer));
    }

    @Test
    void testRemoveNonExistingFavourite() {
        // Attempt to remove a non-existing favourite POI
        userData.removeFavourite(poi, baseMap, floorMap);
        assertTrue(userData.getFavouritePois().isEmpty());
    }

    @Test
    void testAddPoiNullInput() {
        assertThrows(NullPointerException.class, () -> userData.addPoi(null, null, null));
    }


    @Test
    void testRemoveSelectedPOINormalCase() {
        userData.addPoi(baseMap, floorMap, poi);
        CurrentUser.removeSelectedPOI();
        UserLayer userLayer = UserData.findUserLayer(baseMap, floorMap, userData);
        assertNotNull(userLayer);
        assertFalse(userLayer.getPoints().contains(poi));
    }

    @Test
    void testRemoveSelectedPOIWithNullBaseMap() {
        CurrentUser.setCurrentBaseMap(null);
        CurrentUser.removeSelectedPOI();
        // Assert no exception is thrown and the method returns early
    }

    @Test
    void testRemoveSelectedPOIWithNullFloorMap() {
        CurrentUser.setCurrentFloorMap(null);
        CurrentUser.removeSelectedPOI();
        // Assert no exception is thrown and the method returns early
    }

    @Test
    void testRemoveSelectedPOIWithNullPOI() {
        CurrentUser.setCurrentSelectedPoi(null);
        CurrentUser.removeSelectedPOI();
        // Assert no exception is thrown and the method returns early
    }

    @Test
    void testEditPoiNormalCase() {
        userData.addPoi(baseMap, floorMap, poi);
        PointOfInterest updatedPoi = new PointOfInterest();
        CurrentUser.editPoi(updatedPoi);
        UserLayer userLayer = UserData.findUserLayer(baseMap, floorMap, userData);
        assertNotNull(userLayer);
        assertTrue(userLayer.getPoints().contains(updatedPoi));
        assertFalse(userLayer.getPoints().contains(poi));
    }

    @Test
    void testEditPoiWithNullBaseMap() {
        CurrentUser.setCurrentBaseMap(null);
        PointOfInterest updatedPoi = new PointOfInterest();
        CurrentUser.editPoi(updatedPoi);
        // Assert no exception is thrown and the method returns early
    }

    @Test
    void testEditPoiWithNullFloorMap() {
        CurrentUser.setCurrentFloorMap(null);
        PointOfInterest updatedPoi = new PointOfInterest();
        CurrentUser.editPoi(updatedPoi);
        // Assert no exception is thrown and the method returns early
    }
    @Test
    void testEditPoiWithNullUpdatedPoi() {
        userData.addPoi(baseMap, floorMap, poi);
        CurrentUser.editPoi(null);
        UserLayer userLayer = UserData.findUserLayer(baseMap, floorMap, userData);
        assertNotNull(userLayer);
        assertTrue(userLayer.getPoints().contains(poi));
        // Assert no exception is thrown and the method returns early without changing the existing POI
    }

}
