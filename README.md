# advance-final-project

-- i decided to use simulate to event driven architicture based on :

a - copling between component 

b - needing of realtime updates 

1- collectdata logic - when user request to collect data the creation and meta data service publich event using Google Guava EventBus library (publish and subscriber library to simulate event driven architecture ) 
  the subscriber (services that i want to get from it) that publish another type of event to DataCollect Service that contain the data i want to save it .

2- using decorater desgin pattern on services to extend the functionalities of a exisited functionalities and then create Event driven services that add the event type behavior .

3 - delete logic - user send user name and delete type to data delete service that send event contain provided data to each services i want to delete from it and then save the user name to ensure that user cant make new user with same user name.

4 - use singelton pattern in Mock queue and mongo database to enhance performance and memory mangment .

5 - use Templete Pattern in  PDF Convertor because in each ServiceType the format of the PDF could be different, we used a laibrary to convert files to PDF

6 - Facotory Pattern to change between ServiceType and return convertor for each service also this allows flexibility in adding Service Types

7 - use Factory Pattern for CloudStorage for flexibility to add different Storages, using GoogleDrive Java Documentation to upload the ZIP file to the cloud 

https://drive.google.com/file/d/1II7-g0kauuiraxnczONIgaKQ7_fHPV1x/view?usp=sharing

![main](https://github.com/noorhonjol/advance-final-project/assets/29591992/44fc1646-e4e6-4b89-98d3-2a4042b45097)

![systemContext](https://github.com/noorhonjol/advance-final-project/assets/29591992/0ff4dadf-f182-40b3-a8cd-34f03a4b3f49)

![containerDigram](https://github.com/noorhonjol/advance-final-project/assets/29591992/0b474896-9d33-4788-a096-b5151027b2a4)

![exportcomponent](https://github.com/noorhonjol/advance-final-project/assets/29591992/5f176803-1611-4cc2-b02b-2d21c52f8779)

![collectComponent](https://github.com/noorhonjol/advance-final-project/assets/29591992/0cc228ef-41a5-4813-b57d-0c17b993cfe2)

![creationComponent](https://github.com/noorhonjol/advance-final-project/assets/29591992/4a02ed27-3260-4763-988a-e73360f69447)

![deleteComponent](https://github.com/noorhonjol/advance-final-project/assets/29591992/9b1d5985-130d-46cd-9945-48f814c7cc0c)

![useCase](https://github.com/noorhonjol/advance-final-project/assets/29591992/c78550e3-74cf-4cc3-8d94-33aeb3645bf0)

![image](https://github.com/noorhonjol/advance-final-project/assets/29591992/b3326ed3-325e-421b-bfee-ebc5ad66e034)
