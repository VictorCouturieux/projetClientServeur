package client;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by ctx on 11/12/17.
 */
public class Request implements Serializable {

    private String commande;
    private String arg;

    public Request(String req) throws IllegalArgumentException {

        if (!Objects.equals(req, "")) {

            String[] tblReq = req.split(" ");

            if (tblReq.length == 1) {
                if (!(Objects.equals(tblReq[0], "list") || Objects.equals(tblReq[0], "help") || Objects.equals(tblReq[0], "quit"))){
                    throw new IllegalArgumentException("argument non valide");
                }
            } else if (tblReq.length == 2){
                if (!(Objects.equals(tblReq[0], "local") || Objects.equals(tblReq[0], "get") || Objects.equals(tblReq[0], "search"))){
                    throw new IllegalArgumentException("argument non valide");
                } else if (Objects.equals(tblReq[0], "local") && !(Objects.equals(tblReq[1], "list")) ) {
                    throw new IllegalArgumentException("argument non valide");
                }
            } else {
                throw new IllegalArgumentException("le nombre d'argument n'est pas respectee");
            }
        } else {
            throw new IllegalArgumentException("requete invalide");
        }
    }

    public String getCommande() {
        return commande;
    }
    public void setCommande(String commande) {
        this.commande = commande;
    }
    public String getArg() {
        return arg;
    }
    public void setArg(String arg) {
        this.arg = arg;
    }




}
