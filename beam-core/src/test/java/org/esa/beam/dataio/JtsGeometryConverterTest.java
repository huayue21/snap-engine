package org.esa.beam.dataio;

import com.bc.ceres.binding.ConversionException;
import com.bc.ceres.binding.Converter;
import com.bc.ceres.binding.ConverterRegistry;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class JtsGeometryConverterTest {

    private static JtsGeometryConverter converter;
    private static final GeometryFactory factory = new GeometryFactory();

    @BeforeClass
    public static void setUp() throws Exception {
        converter = new JtsGeometryConverter();
        ConverterRegistry.getInstance().setConverter(Geometry.class, converter);
    }

    @Test
    public void testRetrievingConverterFromRegistry() {
        final ConverterRegistry registry = ConverterRegistry.getInstance();
        final Converter<Geometry> geomConverter = registry.getConverter(Geometry.class);
        assertNotNull(geomConverter);
        assertSame(converter, geomConverter);

    }
    @Test
    public void testType() {
        assertEquals(Geometry.class, converter.getValueType());
    }
    
    @Test
    public void testParse() throws ConversionException {
        testParsing(factory.createPoint(new Coordinate(12.4567890, 0.00000001)));
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(2,0),new Coordinate(6,0),
                new Coordinate(8,3),new Coordinate(8,9),
                new Coordinate(6,11),new Coordinate(2,9),
                new Coordinate(2,0),   
        };
        testParsing(factory.createLineString(coordinates));
        final LinearRing linearRing = factory.createLinearRing(coordinates);
        testParsing(linearRing);
        final Coordinate[] hole = new Coordinate[] {
                new Coordinate(4,4),new Coordinate(5,4),
                new Coordinate(5,6),new Coordinate(4,6),
                new Coordinate(4,4),
        };
        final Polygon polygon = factory.createPolygon(linearRing, new LinearRing[]{factory.createLinearRing(hole)});
        testParsing(polygon);
    }

    @Test
    public void testFormat() throws ConversionException, ParseException {
        testFormatting(factory.createPoint(new Coordinate(12.4567890, 0.00000001)));
        final Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(2,0),new Coordinate(6,0),
                new Coordinate(8,3),new Coordinate(8,9),
                new Coordinate(6,11),new Coordinate(2,9),
                new Coordinate(2,0),
        };
        final LinearRing linearRing = factory.createLinearRing(coordinates);
        testFormatting(linearRing);
        final Coordinate[] hole = new Coordinate[] {
                new Coordinate(4,4),new Coordinate(5,4),
                new Coordinate(5,6),new Coordinate(4,6),
                new Coordinate(4,4),
        };
        final Polygon polygon = factory.createPolygon(linearRing, new LinearRing[]{factory.createLinearRing(hole)});
        testFormatting(polygon);

    }

    private void testFormatting(Geometry expectedGeometry) throws ParseException {
        final WKTReader wktReader = new WKTReader();
        final String geometryWkt = converter.format(expectedGeometry);
        final Geometry geometry = wktReader.read(geometryWkt);
        assertTrue(expectedGeometry.equalsExact(geometry));
    }

    private void testParsing(Geometry expectedGeometry) throws ConversionException {
        final WKTWriter wktWriter = new WKTWriter();
        final String geometryWkt = wktWriter.write(expectedGeometry);
        final Geometry geometry = converter.parse(geometryWkt);
        assertTrue(expectedGeometry.equalsExact(geometry));
    }
}
