package com.volmit.iris.util.decree.handlers;

import com.volmit.iris.Iris;
import com.volmit.iris.core.project.loader.IrisData;
import com.volmit.iris.engine.object.dimensional.IrisDimension;
import com.volmit.iris.engine.object.entity.IrisEntity;
import com.volmit.iris.util.collection.KList;
import com.volmit.iris.util.collection.KMap;
import com.volmit.iris.util.decree.DecreeParameterHandler;
import com.volmit.iris.util.decree.exceptions.DecreeParsingException;
import com.volmit.iris.util.decree.exceptions.DecreeWhichException;

import java.io.File;

public class EntityHandler implements DecreeParameterHandler<IrisEntity> {

    /**
     * Should return the possible values for this type
     *
     * @return Possibilities for this type.
     */
    @Override
    public KList<IrisEntity> getPossibilities() {
        KMap<String, IrisEntity> p = new KMap<>();

        //noinspection ConstantConditions
        for(File i : Iris.instance.getDataFolder("packs").listFiles())
        {
            if(i.isDirectory()) {
                IrisData data = new IrisData(i, true);
                for (IrisEntity j : data.getEntityLoader().loadAll(data.getEntityLoader().getPossibleKeys()))
                {
                    p.putIfAbsent(j.getLoadKey(), j);
                }

                data.close();
            }
        }

        return p.v();
    }

    /**
     * Converting the type back to a string (inverse of the {@link #parse(String) parse} method)
     *
     * @param entity The input of the designated type to convert to a String
     * @return The resulting string
     */
    @Override
    public String toString(IrisEntity entity) {
        return entity.getLoadKey();
    }

    /**
     * Should parse a String into the designated type
     *
     * @param in The string to parse
     * @return The value extracted from the string, of the designated type
     * @throws DecreeParsingException Thrown when the parsing fails (ex: "oop" translated to an integer throws this)
     * @throws DecreeWhichException   Thrown when multiple results are possible
     */
    @Override
    public IrisEntity parse(String in) throws DecreeParsingException, DecreeWhichException {
        try
        {
            KList<IrisEntity> options = getPossibilities(in);

            if(options.isEmpty())
            {
                throw new DecreeParsingException("Unable to find Entity \"" + in + "\"");
            }

            else if(options.size() > 1)
            {
                throw new DecreeWhichException();
            }

            return options.get(0);
        }
        catch(DecreeParsingException e){
            throw e;
        }
        catch(Throwable e)
        {
            throw new DecreeParsingException("Unable to find Entity \"" + in + "\" because of an uncaught exception: " + e);
        }
    }

    /**
     * Returns whether a certain type is supported by this handler<br>
     *
     * @param type The type to check
     * @return True if supported, false if not
     */
    @Override
    public boolean supports(Class<?> type) {
        return type.equals(IrisEntity.class);
    }

    @Override
    public String getRandomDefault()
    {
        return "entity";
    }
}