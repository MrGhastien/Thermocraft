package mrghastien.thermocraft.common.network.data;

import mrghastien.thermocraft.common.capabilities.heat.transport.networks.HeatNetworkHandler;
import mrghastien.thermocraft.common.network.NetworkDataType;

/**
    As for the Heat Network system, this is derived from Mekanism.
    {@link HeatNetworkHandler}
 */

public interface INetworkData {

    boolean hasChanged();

    Object get();

    void set(Object value);

    NetworkDataType getType();

    int getId();

    void update();
    /*
    ARCHITECTURE :

    BaseContainer : Liste de INetworkData (valeurs suivies)
    INetworkData : Donnée / propriété d'un objet pouvant être suivie et modifiée
    EN GROS -> Système précédent mais sans Reflection et en évitant les Generics (et les unchecked casts)
    ClassUtils : Récupération de tous les champs annotés par @TrackedByClient, + si objet annoté récupérer les champs de celui-ci

    POUR RECUPERER LES GETTERS / SETTERS : Possibilité de mettre dans l'annotation @TrackedByClient les noms du getter et du setter associés
    Récupérer les fields de la classe (et parents), et une fois peuplés avec l'instance, les transformer en INetworkData
     */

}