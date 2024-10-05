package kr.co.dearmydiary.audit.audit.repository;

import java.math.BigInteger;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.dearmydiary.audit.audit.entity.PrfCategory;

@Repository
public interface PrfCategoryRepo extends JpaRepository<PrfCategory, BigInteger>, JpaSpecificationExecutor<PrfCategory> {

    @Query(value = """
        WITH RECURSIVE AllChildren AS (
            SELECT
                cat_cd
            FROM
                prf_category
            WHERE
                cat_cd = :catCd

            UNION ALL

            SELECT
                p.cat_cd
            FROM
                prf_category p
            INNER JOIN
                AllChildren ac
            ON
                p.up_cat_cd = ac.cat_cd
        )
        SELECT
            p.cat_cd,
            p.cat_nm,
            p.up_cat_cd,
            p.up_cat_nm,
            p.print_yn,
            p.mgt_yn,
            p.del_yn,
            p.in_dt,
            p.in_id,
            p.chg_dt,
            p.chg_id,
            p.cat_seq
        FROM
            prf_category p
        WHERE
            p.cat_cd NOT IN (SELECT cat_cd FROM AllChildren)
            AND (
                p.up_cat_cd IS NULL 
                OR p.up_cat_cd IS NOT NULL
            )
        ORDER BY
            p.cat_cd ASC;
        """, nativeQuery = true)
    List<PrfCategory> findByIdWithParents(@Param("catCd") String catCd);


    @Query(value = """
        with RECURSIVE CategoryHierarchy AS (
            SELECT
                cat_cd,
                cat_nm,
                up_cat_cd,
                up_cat_nm,
                print_yn,
                mgt_yn,
                del_yn,
                in_dt,
                in_id,
                chg_dt,
                chg_id,
                cat_seq
            FROM
                prf_category
            WHERE
                (:catCd IS NULL OR cat_cd = :catCd)
            UNION ALL
            SELECT
                p.cat_cd,
                p.cat_nm,
                p.up_cat_cd,
                p.up_cat_nm,
                p.print_yn,
                p.mgt_yn,
                p.del_yn,
                p.in_dt,
                p.in_id,
                p.chg_dt,
                p.chg_id,
                p.cat_seq
            FROM
                prf_category p
            INNER JOIN
                CategoryHierarchy ch
            ON
                p.up_cat_cd = ch.cat_cd
        )
        SELECT DISTINCT
            *
        FROM
            CategoryHierarchy 
        WHERE
            CASE 
                WHEN ((:catCd IS NULL OR cat_cd <> :catCd) AND ((:mgtYn IS NULL) OR ( mgt_yn = :mgtYn)) AND ((:printYn IS NULL) OR ( print_yn = :printYn)) AND (del_yn = 'n')) THEN 
                    CASE 
                        WHEN ((:searchKey IS NULL) OR (:searchKey = 'catNm')) AND ((:searchStr IS NULL) OR (cat_nm LIKE CONCAT('%', :searchStr, '%'))) THEN 1
                        WHEN ((:searchKey IS NULL) OR (:searchKey = 'catCd')) AND ((:searchStr IS NULL) OR (cat_cd LIKE CONCAT('%', :searchStr, '%'))) THEN 1
                        ELSE 0
                    END
                ELSE 0
            END
        ORDER BY
            cat_cd ASC
        LIMIT :size OFFSET :page
        """, nativeQuery = true)
    List<PrfCategory> findByIdWithChildren(@Param("catCd") String catCd, @Param("mgtYn") Character mgtYn,  @Param("printYn") Character printYn, @Param("searchKey") String searchKey, @Param("searchStr") String searchStr, @Param("page") int page, @Param("size") int size);

    @Query(value = """
        with RECURSIVE CategoryHierarchy AS (
            SELECT
                cat_cd,
                cat_nm,
                up_cat_cd,
                up_cat_nm,
                del_yn,
                cat_seq,
                mgt_yn,
                print_yn
            FROM
                prf_category
            WHERE
                (:catCd IS NULL OR cat_cd = :catCd)
            UNION ALL
            SELECT
                p.cat_cd,
                p.cat_nm,
                p.up_cat_cd,
                p.up_cat_nm,
                p.del_yn,
                p.cat_seq,
                p.mgt_yn,
                p.print_yn
            FROM
                prf_category p
            INNER JOIN
                CategoryHierarchy ch
            ON
                p.up_cat_cd = ch.cat_cd
        )
        SELECT DISTINCT
            COUNT(DISTINCT cat_cd)
        FROM
            CategoryHierarchy
        WHERE
            CASE 
                WHEN ((:catCd IS NULL OR cat_cd <> :catCd) AND ((:mgtYn IS NULL) OR ( mgt_yn = :mgtYn)) AND ((:printYn IS NULL) OR ( print_yn = :printYn)) AND (del_yn = 'n')) THEN 
                    CASE 
                        WHEN ((:searchKey IS NULL) OR (:searchKey = 'catNm')) AND ((:searchStr IS NULL) OR (cat_nm LIKE CONCAT('%', :searchStr, '%'))) THEN 1
                        WHEN ((:searchKey IS NULL) OR (:searchKey = 'catCd')) AND ((:searchStr IS NULL) OR (cat_cd LIKE CONCAT('%', :searchStr, '%'))) THEN 1
                        ELSE 0
                    END
                ELSE 0
            END
        """, nativeQuery = true)
    Integer findByIdWithChildrenCnt(@Param("catCd") String catCd, @Param("mgtYn") Character mgtYn,  @Param("printYn") Character printYn,  @Param("searchKey") String searchKey, @Param("searchStr") String searchStr);

    @Query(value = """
        SELECT
            Max(cat_seq)+1
        FROM
            prf_category
        """, nativeQuery = true)
    Integer findLastCatSeq();

    

}