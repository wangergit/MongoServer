package com.mongodb.dao;
import com.mongodb.DBObject;

import java.util.List;
 
/**
 * Mondodb ��ַ���� �ӿ���
 *
 * @author babylon
 * @version 1.1
 * @date 2016��7��12��-����1:42:06
 */
public interface MongoGeoDao extends MongoDao{
	
/**
 * �ۺϲ�ѯ����ѯһ���㸽���ĵ㣬������ÿһ���㵽�����ĵ�ľ��룬��������Ƭ�������$nearSphere��֧�֣�
 * ����ʹ�ø÷������в�ѯ
 * @param collection    ��������
 * @param query         ��ѯ����
 * @param point         ���ĵ�����
 * @param limit         ���ؼ�¼��������
 * @param maxDistance   ������
 * @return              ��NULL��list
 */
public List<DBObject> geoNear(String collection, DBObject query, Point point,int limit, long maxDistance) ;

/**
 * ��ѯ��Բ�������ڵ�����㣬��Ҫָ�����ĵ�����Ͱ뾶���뾶��λ����
 *
 * @param collection    ��������
 * @param locationField �����ֶ�
 * @param center        ���ĵ�����[���ȣ�γ��]
 * @param radius        �뾶 ��λ:��
 * @param fields        ��ѯ�ֶ�
 * @param query         ��ѯ����
 * @param limit         ���ؼ�¼��������
 * @return              ��NULL��list
 */
public List<DBObject> withinCircle(String collection,String locationField, Point center, long radius,
                                   DBObject fields, DBObject query, int limit);

/**
 * ָ��һ���㣬���ظõ㸽��������������ɽ���Զ,$nearSphere ��Ҫ��������2dsphere ����2d,����֧��GeoJSON��һ�������
 * ע��: $nearSphere�ڷ�Ƭ�ļ�Ⱥ����Ч��ʹ��geoNear
 *
 * @param collection    ��������
 * @param locationField �����ֶ�
 * @param center        ���ĵ�����[���ȣ�γ��]
 * @param minDistance   �������
 * @param maxDistance   ��Զ����
 * @param query         ��ѯ����
 * @param fields        ��ѯ�ֶ�
 * @param limit         ���ؼ�¼��������
 * @return              ��NULL��list
 */
public List<DBObject> nearSphere(String collection, String locationField, Point center, long minDistance, long maxDistance, DBObject query, DBObject fields, int limit);


/**
 * ��ѯλ��ָ��һ����ն�����ڵ���������㣬�����Ķ��������������λ����γɷ�յĶ����
 * ��������
 *       final LinkedList<double[]> polygon = new LinkedList<>();
 *       polygon.addLast(new double[] {  121.36, 31.18 });
 *       polygon.addLast(new double[] {  121.35, 31.36 });
 *       polygon.addLast(new double[] {  121.39, 31.17 });
 *       polygon.addLast(new double[] {  121.36, 31.18 });
 *
 * MongoDB������εı߽�Ҳ��Ϊ��ѯ��״��һ����
 * @param collection    ��������
 * @param locationField �����ֶ�
 * @param polygon       ���������
 * @param fields        ��ѯ�ֶ�
 * @param query         ��ѯ����
 * @param limit         ���ؼ�¼��������
 * @return              ��NULL��list
 */
public List<DBObject> withinPolygon(String collection,String locationField,
                                    List<double[]> polygon,DBObject fields,DBObject query,int limit);


/**
 * ��ѯλ��ָ�������ն�����ڵ���������㣬�����Ķ��������������λ����γɷ�յĶ����
 * @param collection    ��������
 * @param locationField �����ֶ�
 * @param polygons      ��������� ����
 * @param fields        ��ѯ�ֶ�
 * @param query         ��ѯ����
 * @param limit         ���ؼ�¼��������
 * @return              ��NULL��list
 */
public List<DBObject> withinMultiPolygon(String collection,String locationField,
                                    List<List<double[]>> polygons,DBObject fields,DBObject query,int limit);


/**
 * �ھ��������ڲ�������㣬�÷���������2d������֧�֣���2dsphere�в�֧��
 * @param collection    ��������
 * @param locationField �����ֶ�
 * @param bottomLeft    ���½�
 * @param upperRight    ���Ͻ�
 * @param fields        ��ѯ�ֶ�
 * @param query         ��ѯ����
 * @param limit         ���ؼ�¼��������
 * @return              ��NULL��list
 */
@Deprecated
public List<DBObject> withinBox(String collection, String locationField,
                                Point bottomLeft, Point upperRight, DBObject fields, DBObject query,int limit);

}