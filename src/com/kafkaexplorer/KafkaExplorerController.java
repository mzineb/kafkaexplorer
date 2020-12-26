package com.kafkaexplorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.kafkaexplorer.model.Cluster;
import com.kafkaexplorer.model.Clusters;
import com.sun.source.tree.Tree;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class KafkaExplorerController implements Initializable {

@FXML
private TreeView<String> kafkaTree;

@FXML
private AnchorPane mainContent;

private Cluster[] clusters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Load config.yaml file from the /resources folder
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("config.yaml").getFile());

        // Instantiating a new ObjectMapper as a YAMLFactory
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        // Mapping the cluster Array from the YAML file to the Cluster class
        try {
            clusters = om.readValue(file, com.kafkaexplorer.model.Cluster[].class);

            TreeItem<String> root = new TreeItem<>("Kafka Clusters");

            for (int i = 0; i < clusters.length; i++)
            {
                //build kafka cluster tree
                      TreeItem clusterItem = new TreeItem(clusters[i].getName());
                      root.getChildren().add(clusterItem);
            }


        kafkaTree.setRoot(root);
        root.setExpanded(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onMouseClicked(MouseEvent mouseEvent) {

        //Open the topicBrowser screen
        try {
            // Get selected Node
            Node node = mouseEvent.getPickResult().getIntersectedNode();

            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                String clusterName = (String) ((TreeItem) kafkaTree.getSelectionModel().getSelectedItem()).getValue();

                FXMLLoader clusterConfigLoader = new FXMLLoader(getClass().getResource("clusterConfig.fxml"));
                AnchorPane mainRoot = clusterConfigLoader.load();

                ClusterConfigController clusterConfigController = clusterConfigLoader.getController();

                //find selected cluster from Clusters Array
                Cluster selectedCluster = null;

                for (int i = 0; i < clusters.length; i++)
                {
                    if (clusters[i].getName() == clusterName)
                    {
                        selectedCluster = new Cluster(clusters[i]);
                    }
                }

                if (selectedCluster != null) {
                    clusterConfigController.populateScreen(selectedCluster);

                    mainContent.getChildren().setAll(mainRoot);
                }
                else
                {
                   //todo
                    mainContent.getChildren().clear();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}