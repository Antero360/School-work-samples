/*
 * Name:        Nevarez-Lira, Antero (nevarez)
 * Date:        February 19, 2019
 * Course:      CSCI 340
 * Instructor:  Dr. Hansen
 * Description: 
 *              Reads in a file containing a site's pages. Each page has a rank,
 *              link, content, and list of external links. Program strips all
 *              information from file and creates a website object that contains
 *              a list of pages and sets appropriate page ranks. Prompts user for
 *              a search term, queries the site, and displays a list of pages that
 *              contain search term. Displays page rank, in descending order, and
 *              page link.
 *              
 * Bugs:        Minor rounding issues.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class PageRank {

    public static void main(String[] args) {
        
        //initiate website, update page ranks
        Website site = new Website("CS.txt");
        int counter = 0;        
        while (counter < 1000) {
            counter++;
            site.SetPageRank();
        }
        
        //prompt user for term to query
        System.out.println("Enter a search term: ");
        Scanner scanner = new Scanner(System.in);
        String query = scanner.nextLine();
        
        //get all pages that contain query term
        List<Page> results = site.Search(query);
        System.out.println(results.size() + " Hits Found!");
        
        //format output to 10 sig-figs
        DecimalFormat rankFormatter = new DecimalFormat("###.##########");        
        //sort results in descending order, print rank and link of results
        Collections.sort(results);
        Collections.reverse(results);
        for (int x = 0; x < results.size(); x++) {
            System.out.println(String.format("%1$s\t%2$s", rankFormatter.format(results.get(x).GetRank()), results.get(x).GetLink()));
        }        
    }

    static class Website{

        private List<Page> pages;
        
        public Website(String url) {
            pages = new ArrayList<Page>();
            if (url.contains(".txt")) {
                try {
                    ReadFromFile(url);
                } catch (Exception ex) {
                    System.out.println("Error! File does not exist.");
                }
            }
        }

        public List<Page> GetPages() {
            return pages;
        }

        public List<Page> Search(String query) {
            return QueryWebsite(query);
        }

        /*
        * QueryWebsite(String): takes a string as a param, goes through all pages
        * checks if a page's content/url contains param...if so, add to a list of results.
        * return list
        */
        private List<Page> QueryWebsite(String query) {
            List<Page> results = new ArrayList<Page>();
            for (int x = 0; x < pages.size(); x++) {
                if ((pages.get(x).GetLink().contains(query.toLowerCase())) || (pages.get(x).GetContent().toLowerCase().contains(query.toLowerCase()))) {
                    results.add(pages.get(x));
                }
            }
            return results;
        }

        /*
        * ReadFromFile(String): takes a filename as a param, and finds appropriate file
        * reads all info into a stringbuilder, closes file, and works directly with 
        * the stringbuilder. strips appropriate data and creates a list of Pages.
        * Goes through the list of Pages and sets initial rank to (1 / totalPages)
        */
        private void ReadFromFile(String fileName) throws FileNotFoundException {
            StringBuilder textTemp = new StringBuilder();
            Scanner file = new Scanner(new File(fileName));
            while (file.hasNextLine()) {
                textTemp.append(String.format("%1$s\n", file.nextLine()));
            }
            file.close();
            String fileText = textTemp.toString();
            String[] pageBlocksTemp = fileText.split("PAGE\n");
            List<String> pageBlocks = new ArrayList<String>();

            //make sure that there are no empty page blocks
            for (int x = 0; x < pageBlocksTemp.length; x++) {
                if (pageBlocksTemp[x].equals("") == false) {
                    pageBlocks.add(pageBlocksTemp[x]);
                }
            }

            //itereate through every page block and create the appropriate Page
            //set primary page rank to 1 / total pages
            //add to total site pages
            for (int x = 0; x < pageBlocks.size(); x++) {
                Page page = InformationStrip(pageBlocks.get(x));
                page.SetRank((1.0 / pageBlocks.size()));
                pages.add(page);
            }
        }

        private void SetPageRank() {
            //pageRank = ((1-d)/totalPages) + (d * SIGMA(rankOfReferencingPage /totalExternalLinks ) )
            /*
            * pageRank = rank of this page
            * d = a small numeric constant
            * SIGMA = sum of (rankOfReferencingPage/totalExternalLinks) of all pages that reference this page
            * rankOfReferencingPage = rank of page p, that links to this page
            * totalExternalLinks = total number of outgoing pages from page p
             */
            
            /*
            * 
            */
            double d = 0.15;
            int totalPages = pages.size();
            for (int x = 0; x < pages.size(); x++) {
                double sigmaOfRefPages = 0.0;
                List<Page> referencingPages = pages.get(x).GetReferencingPages(pages);
                for (int y = 0; y < referencingPages.size(); y++) {
                    sigmaOfRefPages += (referencingPages.get(y).GetRank() / referencingPages.get(y).GetExternalLinks().size());
                }
                pages.get(x).SetRank(((1.0 - d) / totalPages + d *(sigmaOfRefPages)));
            }
        }
             
        /*
        * InformationStrip: takes a string that contains the format: 
        *                                                               PageUrl
        *                                                               PageContent
        *                                                               OutgoingLinks
        * splits param into a list using carriage return as a delimeter.
        * Checks to see if each item in that list starts with the 
        * http/s protocol... if so, add that to a list of links, else
        * concatenate item to a string. Create a Page object with 
        * stripped information and return.
        */
        private Page InformationStrip(String pageBlock) {
            String[] strippedBlock = pageBlock.split("\n");
            Page page = new Page(strippedBlock[0]);
            for (int x = 1; x < strippedBlock.length; x++) {
                if (strippedBlock[x].startsWith("http://") || strippedBlock[x].startsWith("https://")) {
                    page.AddExternalLink(strippedBlock[x]);
                } else {
                    page.AddContent(strippedBlock[x]);
                }
            }
            return page;
        }
    }

    static class Page implements Comparable<Page> {

        private double rank;
        private String pageLink;
        private StringBuilder pageContent;
        private List<String> externalLinks;

        public Page(String link) {
            pageLink = link;
            pageContent = new StringBuilder();
            externalLinks = new ArrayList<String>();
        }

        public void AddExternalLink(String link) {
            externalLinks.add(link);
        }

        public void AddContent(String content) {
            pageContent.append(content);
        }

        public String GetLink() {
            return pageLink;
        }

        public String GetContent() {
            return pageContent.toString();
        }

        public List<String> GetExternalLinks() {
            return externalLinks;
        }

        public double GetRank() {
            return rank;
        }

        /*
         * GetReferencingPages(List<pages>): takes a list of pages as a param
         * and goes through each page and checks to see if the link of this page
         * is referenced in another page. If so, add to page to list and return
         */
        public List<Page> GetReferencingPages(List<Page> pages) {
            List<Page> referencePages = new ArrayList<Page>();
            for (int x = 0; x < pages.size(); x++) {
                if (pages.get(x).externalLinks.contains(GetLink())) {
                    referencePages.add(pages.get(x));
                }
            }
            return referencePages;
        }

        public void SetRank(double rank) {
            this.rank = rank;
        }

        @Override
        public int compareTo(Page page) {
            int result = 0;
            if(GetRank() == page.GetRank())
                result = 0;
            else if(GetRank() > page.GetRank())
                result = 1;
            else if(GetRank() < page.GetRank())
                result = -1;
            
            return result;
        }
    }
}