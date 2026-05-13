
public class MP3 extends Gadget {

    private int availableMemory;


    public MP3(String model, double price, int weight, String size, int availableMemory) {
        super(model, price, weight, size);
        this.availableMemory = availableMemory;
    }

    /** Returns the available memory on the MP3 player in MB. */
    public int getAvailableMemory() {
        return availableMemory;
    }


    public void downloadMusic(int memoryRequired) {
        if (memoryRequired <= availableMemory) {
            availableMemory -= memoryRequired;
            System.out.println("Download successful. Remaining memory: " + availableMemory + " MB.");
        } else {
            System.out.println("Insufficient memory to download this music.");
        }
    }


    public void deleteMusic(int memoryFreed) {
        availableMemory += memoryFreed;
        System.out.println("Music deleted. Available memory: " + availableMemory + " MB.");
    }

    
    @Override
    public void display() {
        super.display();
        System.out.println("Available Memory: " + availableMemory + " MB");
    }
}
